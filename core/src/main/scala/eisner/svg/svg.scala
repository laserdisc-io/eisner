package eisner

import io.circe.generic.extras.Configuration
import io.dylemma.spac._
import io.dylemma.spac.xml._
import io.dylemma.spac.xml.XMLParser._

package object svg {
  private[this] final val fillParser        = forMandatoryAttribute("fill")
  private[this] final val strokeParser      = forMandatoryAttribute("stroke")
  private[this] final val strokeWidthParser = forOptionalAttribute("stroke-width").map(_.fold(1.0)(_.dbl))
  private[this] final val ellipseParser = {
    fillParser ~ strokeParser ~ strokeWidthParser ~
      forMandatoryAttribute("cx").map(_.dbl) ~
      forMandatoryAttribute("cy").map(_.dbl) ~
      forMandatoryAttribute("rx").map(_.dbl) ~
      forMandatoryAttribute("ry").map(_.dbl)
  }.as(El.Ellipse)
  private[this] final val pathParser    = (fillParser ~ strokeParser ~ strokeWidthParser ~ forMandatoryAttribute("d")).as(El.Path.apply)
  private[this] final val polygonParser = (fillParser ~ strokeParser ~ strokeWidthParser ~ forMandatoryAttribute("points")).as(El.Polygon)
  private[this] implicit final val textParser = {
    forMandatoryAttribute("text-anchor") ~
      forMandatoryAttribute("x").map(_.dbl) ~
      forMandatoryAttribute("y").map(_.dbl) ~
      forMandatoryAttribute("font-family") ~
      forMandatoryAttribute("font-size").map(_.dbl) ~
      fillParser ~
      forText
  }.as(El.Text)
  private[this] final val basicSVGAttrParser = XMLSplitter(
    attr("width") & attr("height") & attr("viewBox").map(ViewBox.unapply(_).getOrElse(ViewBox.empty))
  )
  private[this] final val transformAttrParser = XMLSplitter("svg" \ "g").first.attr("transform")
  private[this] final val titleParser         = XMLSplitter("svg" \ "g" \ "text").as(textParser)
  private[this] final def elParser =
    XMLSplitter("svg" \ "g" \ "g" \ extractElemName).map {
      case "ellipse" => ellipseParser.map(_ :: Nil)
      case "path"    => pathParser.map(_ :: Nil)
      case "polygon" => polygonParser.map(_ :: Nil)
      case "text"    => textParser.map(_ :: Nil)
      case _         => Parser.constant(Nil)
    }.flatten
  private[this] final val svgParser = basicSVGAttrParser
    .map { case ((w, h), vb) =>
      transformAttrParser.followedBy { t =>
        titleParser.parseFirst.followedBy(title => elParser.parseToList.map(es => SVG(w, h, vb, t, List.empty, title :: es)))
      }
    }
    .wrapSafe
    .map(_.toEither.left.map(e => SVGParserError(e.getLocalizedMessage())))
    .parseFirst

  final def toSVG(s: String): SVGParserError | SVG = svgParser.parse(s)

  implicit final class StringToDouble(private val s: String) extends AnyVal {
    final def dbl: Double = s.toDouble
  }

  implicit final val configuration: Configuration = Configuration.default.withDiscriminator("type")
}
