package object eisner {
  final type DiGraph = dot.DiGraph
  final type SVG     = svg.SVG
  final type Node    = xml.Node

  final val DiGraph  = dot.DiGraph
  final val Subgraph = dot.Subgraph
  final val Edge     = dot.Edge
  final val Topic    = dot.Topic
  final val Store    = dot.Store
  final val SVG      = svg.SVG

  implicit final class IntOps(private val i: Int) extends AnyVal {
    final def tabs: String = "\t" * i
  }
  implicit final class StringOps(private val s: String) extends AnyVal {
    final def dot: DiGraph             = eisner.dot.toDot(s)
    final def dotString: String        = dot.dotString
    final def svg: Either[String, SVG] = io.circe.parser.decode[SVG](s).left.map(_.getLocalizedMessage())
  }
  implicit final class DotOps(private val dg: DiGraph) extends AnyVal {
    final def dotString: String                  = Writer[DiGraph].write(dg, 0).mkString("\n")
    final def svg: scala.concurrent.Future[Node] = eisner.js.toSVG(dotString)
  }
  implicit final class SVGOps(private val svg: SVG) extends AnyVal {
    final def toJson: io.circe.Json = io.circe.Encoder[SVG].apply(svg)
    final def toJsonString: String  = toJson.noSpaces
  }
}
