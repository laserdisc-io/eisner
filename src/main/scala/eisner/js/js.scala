package eisner

import java.io.PrintWriter
import javax.script.{Invocable, ScriptEngineManager}
import java.util.concurrent.CompletionStage
import java.util.{Map => JMap}

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
  private[eisner] final def svgToRoughSVG(svg: String): String =
    engine.invokeFunction("svgToRoughSVG", svg).asInstanceOf[String]
}
