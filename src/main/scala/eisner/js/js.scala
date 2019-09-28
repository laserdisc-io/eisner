package eisner

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
    e.eval("load('classpath:net/arnx/nashorn/lib/promise.js')")
    e.eval("load('classpath:viz.js')")
    e.eval("load('classpath:lite.render.js')")
    e.eval {
      """
      |var viz = function(dot) {
      |  return new Viz().renderString(dot)._future;
      |};
      |""".stripMargin
    }
    e.asInstanceOf[Invocable]
  }

  private[this] final def viz(dot: String): Future[String] =
    engine.invokeFunction("viz", dot).asInstanceOf[CompletionStage[JMap[String, Object]]].toScala.flatMap {
      case jm => Option(jm.get("result")) match {
        case Some(s: String) => Future.successful(s)
        case other => Future.failed(new RuntimeException(s"Could not find svg in viz.js response: $other"))
      }
    }

  final def toSVG(dot: String): Future[Node] = viz(dot).flatMap(svg => Future.fromTry(Try(XML.loadString(svg))))
}
