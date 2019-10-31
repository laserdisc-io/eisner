package eisner

import io.circe.Json

import java.io.PrintWriter
import javax.script.{Invocable, ScriptEngineManager}
import java.util.concurrent.CompletionStage
import java.util.{Map => JMap}

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scala.xml.XML

package object js {
  private[this] val engine: Invocable = {
    val e = new ScriptEngineManager().getEngineByExtension("js")
    e.getContext.setWriter(new PrintWriter(System.out, true))
    e.getContext.setErrorWriter(new PrintWriter(System.err, true))
    e.eval("load('classpath:object.assign.js')")
    e.eval("load('classpath:net/arnx/nashorn/lib/promise.js')")
    e.eval("load('classpath:viz.js')")
    e.eval("load('classpath:lite.render.js')")
    e.eval("load('classpath:rough.es5.js')")
    e.eval("load('classpath:main.js')")
    e.asInstanceOf[Invocable]
  }

  private[eisner] final def dotToSVG(dot: String): Future[String] =
    engine.invokeFunction("dotToSVG", dot).asInstanceOf[CompletionStage[JMap[String, Object]]].toScala.flatMap {
      case jm =>
        Option(jm.get("result")) match {
          case Some(s: String) => Future.successful(s)
          case other           => Future.failed(new RuntimeException(s"Could not find svg in viz.js response: $other"))
        }
    }
  private[this] final def svgToRoughSVG(svg: String): String =
    engine.invokeFunction("svgToRoughSVG", svg).asInstanceOf[String]

  final def toSVG(dot: String): Future[Node] = dotToSVG(dot).flatMap { svgString =>
    Try(XML.loadString(svgString)).toEither.left.map(_.getLocalizedMessage()).flatMap { svgXML =>
      SVG.fromXML(svgXML).flatMap(svg => svgToRoughSVG(svg.toJsonString).svg.map(_.toXML))
    } match {
      case Left(msg) => Future.failed(new RuntimeException(msg))
      case Right(v)  => Future.successful(v)
    }
  }
}
