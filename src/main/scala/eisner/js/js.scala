package eisner

import java.io.PrintWriter
import javax.script.{Invocable, ScriptEngineManager}
import java.util.concurrent.CompletionStage
import java.util.{Map => JMap}

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

package object js {
  private[this] val engine: Invocable = {
    val e = new ScriptEngineManager().getEngineByExtension("js")
    e.getContext.setWriter(new PrintWriter(System.out, true))
    e.getContext.setErrorWriter(new PrintWriter(System.err, true))
    val assign  = Source.fromInputStream(getClass.getResourceAsStream("/object.assign.js")).mkString
    val promise = Source.fromInputStream(getClass.getResourceAsStream("/net/arnx/nashorn/lib/promise.js")).mkString
    val viz     = Source.fromInputStream(getClass.getResourceAsStream("/viz.js")).mkString
    val render  = Source.fromInputStream(getClass.getResourceAsStream("/lite.render.js")).mkString
    val rough   = Source.fromInputStream(getClass.getResourceAsStream("/rough.es5.js")).mkString
    val main    = Source.fromInputStream(getClass.getResourceAsStream("/main.js")).mkString
    e.eval(assign)
    e.eval(promise)
    e.eval(viz)
    e.eval(render)
    e.eval(rough)
    e.eval(main)
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
