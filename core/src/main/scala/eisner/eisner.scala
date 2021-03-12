import guru.nidi.graphviz.engine._
import guru.nidi.graphviz.engine.Format.SVG_STANDALONE
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.parse._
import guru.nidi.graphviz.rough.Roughifyer

import scala.util.Try

package object eisner {
  final type |[+A, +B] = Either[A, B]
  final type DiGraph   = dot.DiGraph
  final type SVG       = svg.SVG

  private[eisner] final val DiGraph  = dot.DiGraph
  private[eisner] final val SubGraph = dot.SubGraph
  private[eisner] final val Edge     = dot.Edge
  private[eisner] final val Topic    = dot.Topic
  private[eisner] final val Store    = dot.Store

  private[this] final val decimalsPattern = """\.(\d{2})\d+(\D?)""".r
  private[this] object trimDoubles extends shapeless.poly.->((s: String) => decimalsPattern.replaceAllIn(s, ".$1$2"))
  private[this] final def simplify(svg: SVG): SVG = {
    val simplified = shapeless.everywhere(trimDoubles)(svg)
    simplified
  }

  private[this] final object parse {
    final val parser = new Parser

    def apply(s: String): GraphvizError | MutableGraph = Try(parser.read(s)).toEither.left.map(t => GraphvizError(t.getMessage()))
  }

  private[this] final lazy val roughifyer = new Roughifyer().bowing(1.2).curveStepCount(1.4).roughness(1).font("*", "sans-serif")

  // FIXME replace this ugly hack
  private[this] final val e2r = (_: String).replaceAll("""\[style = filled, color = white\]""", "[shape = rectangle, color = black]")

  implicit final class StringOps(private val self: String) extends AnyVal {
    private[eisner] final def toDiGraph(c: Config): TopologyParserError | DiGraph = dot.toDot(c, self)

    final def toDot(c: Config): TopologyParserError | String = toDiGraph(c).map(_.dot)
    final def toSVG(c: Config): EisnerError | String         = toDot(c).flatMap(parse(_).flatMap(_.svg))
    final def toRoughSVG(c: Config): EisnerError | String = for {
      dot          <- toDot(c)
      mutableGraph <- parse(e2r(dot))
      graphvizSVG  <- mutableGraph.graphviz.processor(roughifyer).svg
      svgModel     <- svg.toSVG(graphvizSVG)
    } yield svgModel.simplified.svg
  }
  private[eisner] implicit final class DiGraphOps(private val self: DiGraph) extends AnyVal {
    final def dot: String = self.write(0).mkString("\n")
  }
  private[eisner] implicit final class GraphvizOps(private val self: Graphviz) extends AnyVal {
    final def svg: GraphvizError | String = Try(self.render(SVG_STANDALONE).toString).toEither.left.map(t => GraphvizError(t.getMessage))
  }
  private[eisner] implicit final class IntOps(private val self: Int) extends AnyVal {
    final def tabs: String = "\t" * self
  }
  private[eisner] implicit final class MutableGraphOps(private val self: MutableGraph) extends AnyVal {
    final def graphviz: Graphviz          = Graphviz.fromGraph(self)
    final def svg: GraphvizError | String = graphviz.svg
  }
  private[eisner] implicit final class SVGOps(private val self: SVG) extends AnyVal {
    def simplified: SVG = simplify(self)
    def svg: String     = self.write(0).mkString("\n")
  }
  private[eisner] implicit final class WriterOps[A](private val self: A) extends AnyVal {
    final def write(tabs: Int)(implicit A: Writer[A]): List[String] = A.write(self, tabs)
  }
}
