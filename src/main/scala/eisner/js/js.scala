package eisner

import java.io.PrintWriter
import javax.script.{Invocable, ScriptEngineManager}
import java.util.concurrent.CompletionStage
import java.util.{Map => JMap}

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

package object js {
  import dot._

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

  implicit final class GraphToSVG(private val g: Graph) extends AnyVal {
    final def svg: Future[String] =
      engine.invokeFunction("viz", Writer[Graph].write(g, 0).mkString("\n")).asInstanceOf[CompletionStage[JMap[String, Object]]].toScala.map(_.get("result").asInstanceOf[String])
  }
}
