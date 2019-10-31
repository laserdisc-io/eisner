package object eisner {
  final type DiGraph = dot.DiGraph
  final type SVG     = svg.SVG

  private[eisner] final val DiGraph  = dot.DiGraph
  private[eisner] final val Subgraph = dot.Subgraph
  private[eisner] final val Edge     = dot.Edge
  private[eisner] final val Topic    = dot.Topic
  private[eisner] final val Store    = dot.Store

  implicit final class IntOps(private val i: Int) extends AnyVal {
    final def tabs: String = "\t" * i
  }
  implicit final class StringOps(private val s: String) extends AnyVal {
    final def dot: DiGraph             = eisner.dot.toDot(s)
    final def dotString: String        = dot.dotString
    final def svg: Either[String, SVG] = io.circe.parser.decode[SVG](s).left.map(_.getLocalizedMessage())
  }
  implicit final class DotOps(private val dg: DiGraph) extends AnyVal {
    final def dotString: String = Writer[DiGraph].write(dg, 0).mkString("\n")
    final def svg(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[String] = {
      eisner.js.dotToSVG(dotString).flatMap { svgString =>
        eisner.svg.toSVG(svgString).flatMap(svg => eisner.js.svgToRoughSVG(svg.toJsonString).svg.map(_.xmlString)) match {
          case Left(msg) => scala.concurrent.Future.failed(new RuntimeException(msg))
          case Right(v)  => scala.concurrent.Future.successful(v)
        }
      }
    }
  }
  implicit final class SVGOps(private val svg: SVG) extends AnyVal {
    final def toJson: io.circe.Json = io.circe.Encoder[SVG].apply(svg)
    final def toJsonString: String  = toJson.noSpaces
    final def xmlString: String     = Writer[SVG].write(svg, 0).mkString("\n")
  }
}
