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
    val eisnerRoughSVG        = settingKey[Boolean]("The flag that controls whether to create SVG using pseudo hand-drawing")
    val eisnerTargetDirectory = settingKey[File]("The directory where to store the generated topologies")
    val eisnerTopologies      = settingKey[Seq[String]]("The fully qualified names of classes implementing org.apache.kafka.streams.Topology")
    val eisnerTopologiesSnippet =
      settingKey[Option[String]]("A scala snippet (including all imports) that evaluates to a Seq[(String, org.apache.kafka.streams.Topology)]")
    val eisner = taskKey[Set[File]]("Generates one SVG for each org.apache.kafka.streams.Topology")
  }

  import autoImport._

  override final def projectSettings: Seq[Setting[_]] = Seq(
    eisnerRoughSVG := true,
    eisnerTargetDirectory := (Compile / target).value / "eisner",
    eisnerTopologies := Seq.empty,
    eisnerTopologiesSnippet := None,
    eisner := generate.dependsOn(Compile / compile).value
  )

  private[this] final def generate: Def.Initialize[Task[Set[File]]] = Def.taskDyn {
    val log      = streams.value.log
    val cacheDir = streams.value.cacheDirectory

    Def.task {
      val cd  = (Compile / classDirectory).value
      val dcp = (Compile / dependencyClasspath).value.map(_.data)
      val scp = (Compile / scalaInstance).value.allJars

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

      if (topologyDescriptions.nonEmpty) {
        val topologiesWithDots = topologyDescriptions.map { case (n, td) => (n, td, td.toDot) }.collect { case (n, td, Right(d)) => (n, td, d) }
        val inputs = topologiesWithDots.map {
          case (name, _, dot) =>
            val f = new File(s"$cacheDir/${dotsToSlashes(name)}.dot")
            IO.write(f, dot)
            f
        }.toSet
        val cachedFun = FileFunction.cached(cacheDir, FileInfo.hash) { _ =>
          val fs = Future.traverse(topologiesWithDots) {
            case (topologyName, topology, _) =>
              val svg = if (eisnerRoughSVG.value) topology.toSimplifiedRoughSVG else topology.toSVG
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
    }
  }
}
