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

  implicit final class IntOps(private val i: Int) extends AnyVal {
    final def tabs: String = "\t" * i
  }
  implicit final class StringOps(private val s: String) extends AnyVal {
    import io.circe.parser.decode
    import scala.concurrent.{ExecutionContext, Future}

    private[eisner] final def toDiGraph(c: Config): TopologyParserError | DiGraph = dot.toDot(c, s)
    private[eisner] final def decodeSVG: SVGParserError | SVG                     = decode[SVG](s).left.map(e => SVGParserError(e.getLocalizedMessage()))

    final def toDot(c: Config): TopologyParserError | String = toDiGraph(c).map(_.dot)
    final def toSVG(c: Config)(implicit ec: ExecutionContext): Future[String] =
      toDiGraph(c) match {
        case Left(tpe) => Future.failed(tpe)
        case Right(dg) => dg.simpleSVG.map(_.xml)
      }
    final def toRoughSVG(c: Config)(implicit ec: ExecutionContext): Future[String] =
      toDiGraph(c) match {
        case Left(tpe) => Future.failed(tpe)
        case Right(dg) => dg.simpleSVG.flatMap(_.roughSVG.fold(Future.failed, svg => Future.successful(svg.xml)))
      }
    final def toSimplifiedRoughSVG(c: Config)(implicit ec: ExecutionContext): Future[String] =
      toDiGraph(c) match {
        case Left(tpe) => Future.failed(tpe)
        case Right(dg) => dg.simpleSVG.flatMap(_.roughSVG.fold(Future.failed, svg => Future.successful(svg.simplified.xml)))
      }
  }
  private[eisner] implicit final class DiGraphOps(private val dg: DiGraph) extends AnyVal {
    import scala.concurrent.{ExecutionContext, Future}

    final def dot: String = Writer[DiGraph].write(dg, 0).mkString("\n")
    final def simpleSVG(implicit ec: ExecutionContext): Future[SVG] =
      js.dotToSVG(dot).flatMap {
        svg.toSVG(_).fold(Future.failed, Future.successful)
      }
  }
  private[eisner] implicit final class SVGOps(private val svg: SVG) extends AnyVal {
    import io.circe.Encoder

    final def simplified: SVG                = simplify(svg)
    final def json: String                   = Encoder[SVG].apply(svg).noSpaces
    final def xml: String                    = Writer[SVG].write(svg, 0).mkString("\n")
    final def roughSVG: SVGParserError | SVG = js.svgToRoughSVG(json).decodeSVG
  }
}
