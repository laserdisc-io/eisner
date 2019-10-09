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
  private[this] final object ViewBox {
    final def unapply(s: String): Option[(Double, Double)] = s.split(' ').toList match {
      case _ :: _ :: w :: h :: Nil => Try(w.toDouble -> h.toDouble).toOption
      case _                       => None
    }
  }
  private[this] val engine: Invocable = {
    val e = new ScriptEngineManager().getEngineByExtension("js")
    e.getContext.setWriter(new PrintWriter(System.out, true))
    e.getContext.setErrorWriter(new PrintWriter(System.err, true))
    e.eval("load('classpath:net/arnx/nashorn/lib/promise.js')")
    e.eval("load('classpath:viz.js')")
    e.eval("load('classpath:lite.render.js')")
    e.eval("load('classpath:rough.es5.js')")
    e.eval {
      """
      |var viz = function(dot) {
      |  return new Viz().renderString(dot)._future;
      |}
      |
      |var r = function(g0) {
      |  var g = JSON.parse(g0);
      |  var res = [];
      |  var gen = rough.generator({}, { width: g.width, height: g.height });
      |  g.elements.forEach(function(el) {
      |    if (el.type == 'path') {
      |      res.push(gen.toPaths(gen.path(el.d, el.options)));
      |    } else if (el.type == 'ellipse') {
      |      res.push(gen.toPaths(gen.ellipse(el.cx, el.cy, el.rx, el.ry, el.options)));
      |    }
      |  });
      |  return res;
      |}
      |""".stripMargin
    }
    e.asInstanceOf[Invocable]
  }

  private[this] final def viz(dot: String): Future[String] =
    engine.invokeFunction("viz", dot).asInstanceOf[CompletionStage[JMap[String, Object]]].toScala.flatMap {
      case jm =>
        Option(jm.get("result")) match {
          case Some(s: String) => Future.successful(s)
          case other           => Future.failed(new RuntimeException(s"Could not find svg in viz.js response: $other"))
        }
    }
  final def parse(n: Node): G = {
    (n \@ "viewBox") match {
      case ViewBox(w, h) => G(w, h, List.empty)
    }
  }
  // private[this] final def rough(svg: String): Future[Node] = {
  //   Try(XML.loadString(svg))
  // }

  final def toSVG(dot: String): Future[Node] = viz(dot).flatMap(svg => Future.fromTry(Try(XML.loadString(svg))))
}
