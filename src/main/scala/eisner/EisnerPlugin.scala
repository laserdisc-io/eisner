package eisner

import java.net.URLClassLoader

import net.arnx.nashorn.lib.PromiseException
import org.apache.kafka.streams.Topology
import org.clapper.classutil.{ClassFinder, ClassInfo}
import sbt._
import sbt.Keys._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object EisnerPlugin extends AutoPlugin {
  object autoImport {
    val eisnerTopologies = settingKey[Seq[String]]("The fully qualified names of classes implementing org.apache.kafka.streams.Topology")
    val eisner           = taskKey[Seq[File]]("Generates one SVG for each org.apache.kafka.streams.Topology")
  }

  import autoImport._

  override final def projectSettings: Seq[Setting[_]] = Seq(
    eisnerTopologies := Seq.empty,
    eisner := generate.dependsOn(Compile / compile).value
  )

  private[this] final val dotRegex = "\\.".r
  private[this] final val kafkaTopologyName = classOf[Topology].getName
  private[this] final def topologyReflection(log: Logger, cl: ClassLoader) = (topology: ClassInfo) => {
    val name = topology.name
    if (name != kafkaTopologyName) {
      val tClass     = Class.forName(name, true, cl)
      val describeM  = tClass.getSuperclass.getDeclaredMethod("describe")
      val tDescr     = describeM.invoke(tClass.getDeclaredConstructor().newInstance())
      val subtopoM   = tDescr.getClass.getDeclaredMethod("subtopologies")
      val setSubtopo = subtopoM.invoke(tDescr)
      val isEmptyM   = setSubtopo.getClass.getSuperclass.getDeclaredMethod("isEmpty")
      isEmptyM.setAccessible(true)
      val nonEmpty = !isEmptyM.invoke(setSubtopo).asInstanceOf[Boolean]
      isEmptyM.setAccessible(false)

      if (nonEmpty) {
        log.info(s"Eisner - found topology: $name")
        (name -> tDescr.toString) :: Nil
      } else {
        log.info(s"Eisner - skipping empty topology: $name")
        Nil
      }
    } else Nil
  }
  private[this] final def dotsToSlashes(s: String) = dotRegex.replaceAllIn(s, "/")

  private[this] final def generate: Def.Initialize[Task[Seq[File]]] = Def.taskDyn {
    val log      = streams.value.log
    val cacheDir = streams.value.cacheDirectory

    Def.task {
      val cd  = (Compile / classDirectory).value
      val dcp = (Compile / dependencyClasspath).value.map(_.data)

      val cp = cd +: dcp

      log.debug(s"Eisner - looking for topologies in classpath: ${cp.map(_.getAbsolutePath).mkString(":")}")

      val allTopologies = ClassFinder.concreteSubclasses(classOf[Topology], ClassFinder(cp).getClasses)

      val topologies = eisnerTopologies.value match {
        case Seq() => allTopologies
        case ts    => allTopologies.filter(ci => ts.contains(ci.name))
      }

      val cl = new URLClassLoader(cp.map(_.asURL).toArray)

      val topologyDescriptions = topologies.flatMap(topologyReflection(log, cl)).toSeq

      // Nashorn requires classloader that contains referenced classes to be injected
      // see https://stackoverflow.com/a/30251930
      Thread.currentThread.setContextClassLoader(classOf[PromiseException].getClassLoader)

      if (topologyDescriptions.nonEmpty) {
        val fs = Future.traverse(topologyDescriptions) {
          case (topologyName, topology) =>
            topology.toSVG.map { svg =>
              val f = new File(s"$cacheDir/${dotsToSlashes(topologyName)}.svg")
              IO.write(f, svg)
              f
            }
        }
        Await.result(fs, 60.seconds)  
      } else {
        log.warn("Eisner - No topology found!")
        Seq.empty
      }
    }
  }
}
