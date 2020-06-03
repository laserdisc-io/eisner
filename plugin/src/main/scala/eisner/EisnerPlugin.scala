package eisner

import java.net.URLClassLoader

import net.arnx.nashorn.lib.PromiseException
import org.clapper.classutil.ClassFinder
import sbt._
import sbt.Keys._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object EisnerPlugin extends AutoPlugin with ReflectionSupport with SnippetSupport {
  object autoImport {
    val eisnerColorSink = settingKey[String]("The color used to represent sinks, see https://www.graphviz.org/doc/info/colors.html")
    val eisnerColorSubtopology =
      settingKey[String]("The color used to frame subtopologies, see https://www.graphviz.org/doc/info/colors.html")
    val eisnerColorTopic      = settingKey[String]("The color used to represent topics, see https://www.graphviz.org/doc/info/colors.html")
    val eisnerRoughSVG        = settingKey[Boolean]("The flag that controls whether to create SVG using pseudo hand-drawing")
    val eisnerTargetDirectory = settingKey[File]("The directory where to store the generated topologies")
    val eisnerTopologies      = settingKey[Seq[String]]("The fully qualified names of classes implementing org.apache.kafka.streams.Topology")
    val eisnerTopologiesSnippet =
      settingKey[Option[String]](
        "A scala snippet (including all imports) that evaluates to a Seq[(String, org.apache.kafka.streams.Topology)]"
      )
    val eisner = taskKey[Set[File]]("Generates one SVG for each org.apache.kafka.streams.Topology")
  }

  import autoImport._

  override final def projectSettings: Seq[Setting[_]] =
    Seq(
      eisnerColorSink := "black",
      eisnerColorSubtopology := "lightgrey",
      eisnerColorTopic := "black",
      eisnerRoughSVG := true,
      eisnerTargetDirectory := (Compile / target).value / "eisner",
      eisnerTopologies := Seq.empty,
      eisnerTopologiesSnippet := None,
      eisner := generate.dependsOn(Compile / compile).value
    )

  private[this] final def generate: Def.Initialize[Task[Set[File]]] =
    Def.taskDyn {
      val log      = streams.value.log
      val cacheDir = streams.value.cacheDirectory

      Def.task {
        val cd  = (Compile / classDirectory).value
        val dcp = (Compile / dependencyClasspath).value.map(_.data)
        val scp = (Compile / scalaInstance).value.allJars

        // Let's capture the original classloader associated to the current thread
        val originalClassloader = Thread.currentThread.getContextClassLoader

        val topologyDescriptions = eisnerTopologiesSnippet.value match {
          case None =>
            val cp = dcp.filter(f => f.getName.contains("kafka") || f.getName.contains("slf4j")) :+ cd

            log.debug(s"Eisner - looking for topologies in classpath: ${cp.map(_.getAbsolutePath).mkString(":")}")

            val classesToScan = eisnerTopologies.value match {
              case Seq() => ClassFinder(cp).getClasses
              case ts    => ClassFinder(cp).getClasses.filter(ci => ts.contains(ci.name))
            }

            val cl = new URLClassLoader(cp.map(_.asURL).toArray)

            classesToScan.flatMap(findTopologies(log, cl)).toSeq

          case Some(snippet) => getTopologies(snippet, scp, dcp :+ cd)
        }

        // Nashorn requires classloader that contains referenced classes to be injected
        // see https://stackoverflow.com/a/30251930
        Thread.currentThread.setContextClassLoader(classOf[PromiseException].getClassLoader)

        val result: Set[File] = if (topologyDescriptions.nonEmpty) {
          val config = Config(eisnerColorSubtopology.value, eisnerColorTopic.value, eisnerColorSink.value)
          val topologiesWithDots = topologyDescriptions
            .map {
              case (n, td) => (n, td, td.toDot(config))
            }
            .collect {
              case (n, td, Right(d)) => (n, td, d)
            }
          val inputs = topologiesWithDots.map {
            case (name, _, dot) =>
              val f = new File(s"$cacheDir/${dotsToSlashes(name)}.dot")
              IO.write(f, dot)
              f
          }.toSet
          val cachedFun = FileFunction.cached(cacheDir, FileInfo.hash) { _ =>
            val fs = Future.traverse(topologiesWithDots) {
              case (topologyName, topology, _) =>
                val svg = if (eisnerRoughSVG.value) topology.toSimplifiedRoughSVG(config) else topology.toSVG(config)
                svg.map { svg =>
                  val filename = s"${eisnerTargetDirectory.value.getAbsolutePath}/${dotsToSlashes(topologyName)}.svg"
                  log.info(s"Eisner - saving $filename")
                  val f = new File(filename)
                  IO.write(f, svg)
                  f
                }
            }
            Await.result(fs, 60.seconds).toSet
          }
          cachedFun(inputs)
        } else {
          log.warn("Eisner - No topology found!")
          Set.empty
        }

        // re-set the current thread's classloader to what we captured at the start
        Thread.currentThread.setContextClassLoader(originalClassloader)

        result
      }
    }
}
