package eisner

import io.circe.generic.extras.Configuration

import scala.util.Try
import scala.xml._

package object svg {
  private[this] final def parseEllipse(n: Node): El.Ellipse =
    El.Ellipse((n \@ "cx").dbl, (n \@ "cy").dbl, (n \@ "rx").dbl, (n \@ "ry").dbl, n \@ "stroke", 1, n \@ "fill")
  private[this] final def parsePath(n: Node): El.Path       = El.Path(n \@ "d", n \@ "stroke", 1, n \@ "fill")
  private[this] final def parsePolygon(n: Node): El.Polygon = El.Polygon(n \@ "points", n \@ "stroke", 1, n \@ "fill")
  private[this] final def parseText(n: Node): El.Text =
    El.Text((n \@ "x").dbl, (n \@ "y").dbl, n \@ "text-anchor", n \@ "font-family", (n \@ "font-size").dbl, n \@ "fill", n.text)

  final def toSVG(s: String): Either[String, SVG] =
    Try {
      val n           = XML.loadString(s)
      val w           = n \@ "width"
      val h           = n \@ "height"
      val ViewBox(vb) = n \@ "viewBox"
      val t           = n \ "g" \@ "transform"
      val es = (n \\ "_").collect {
        case n @ Elem(_, "ellipse", _, _, _*) => parseEllipse(n)
        case n @ Elem(_, "path", _, _, _*)    => parsePath(n)
        case n @ Elem(_, "polygon", _, _, _*) => parsePolygon(n)
        case n @ Elem(_, "text", _, _, _*)    => parseText(n)
      }.toList
      SVG(w, h, vb, t, List.empty, es)
    }.toEither.left.map(_.getLocalizedMessage)

  implicit final class StringToDouble(private val s: String) extends AnyVal {
    final def dbl: Double = s.toDouble
  }

  implicit final val configuration: Configuration = Configuration.default.withDiscriminator("type")
}
