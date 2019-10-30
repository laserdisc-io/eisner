package eisner
package svg

import io.circe.{Codec, Decoder, Encoder, Json}
import io.circe.generic.extras.semiauto._

import scala.util.{Failure, Try}
import scala.xml._

final case class ViewBox(x: Double, y: Double, w: Double, h: Double)
final object ViewBox {
  final def unapply(s: String): Option[ViewBox] = s.split(' ').toList match {
    case x :: y :: w :: h :: Nil => Try(ViewBox(x.toDouble, y.toDouble, w.toDouble, h.toDouble)).toOption
    case _                       => None
  }

  implicit final val viewBoxCodec: Codec[ViewBox] = deriveConfiguredCodec
}

final case class Pattern(id: String, x: Double, y: Double, width: Double, height: Double, patternUnits: String, path: El.Path)
object Pattern {
  implicit final val patternCodec: Codec[Pattern] = deriveConfiguredCodec
}

sealed trait El extends Product with Serializable
object El {
  final case class Ellipse(cx: Double, cy: Double, rx: Double, ry: Double, stroke: String, strokeWidth: Double, fill: String)    extends El
  final case class Path(d: String, stroke: String, strokeWidth: Double, fill: String)                                            extends El
  final case class Polygon(points: String, stroke: String, strokeWidth: Double, fill: String)                                    extends El
  final case class Text(x: Double, y: Double, anchor: String, fontFamily: String, fontSize: Double, fill: String, value: String) extends El

  object Path {
    implicit final val pathCodec: Codec[Path] = deriveConfiguredCodec
  }

  implicit final val elCodec: Codec[El] = deriveConfiguredCodec
}

final case class SVG(width: String, height: String, viewBox: ViewBox, transform: String, patterns: List[Pattern], elements: List[El]) {
  private[this] def pathToXML(p: El.Path): Node = <path d={p.d} stroke={p.stroke.toString} stroke-width={p.strokeWidth.toString} fill={p.fill}/>
  def toXML: Node =
    <svg width={width} height={height} viewBox={viewBox.x + " " + viewBox.y + " " + viewBox.w + " " + viewBox.h} xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
    <defs>
    {
      patterns.collect {
        case Pattern(id, x, y, w, h, pu, p) =>
          <pattern id={id} x={x.toString} y={y.toString} width={w.toString} height={h.toString} patternUnits={pu}>
          {pathToXML(p)}
        </pattern>

      }
    }
    </defs>
    <g transform={transform}>
    {
      elements.collect {
        case p: El.Path => pathToXML(p)
        case El.Text(x, y, a, ff, fs, f, v) =>
          <text text-anchor={a} x={x.toString} y={y.toString} font-family={ff} font-size={fs.toString} fill={f}>{v}</text>
      }
    }
    </g>
  </svg>
}
final object SVG {
  private[this] final def parseEllipse(n: Node): El.Ellipse =
    El.Ellipse((n \@ "cx").toDouble, (n \@ "cy").toDouble, (n \@ "rx").toDouble, (n \@ "ry").toDouble, n \@ "stroke", 1, n \@ "fill")
  private[this] final def parsePath(n: Node): El.Path       = El.Path(n \@ "d", n \@ "stroke", 1, n \@ "fill")
  private[this] final def parsePolygon(n: Node): El.Polygon = El.Polygon(n \@ "points", n \@ "stroke", 1, n \@ "fill")
  private[this] final def parseText(n: Node): El.Text =
    El.Text((n \@ "x").toDouble, (n \@ "y").toDouble, n \@ "text-anchor", n \@ "font-family", (n \@ "font-size").toDouble, n \@ "fill", n.text)

  final def fromXML(n: Node): Either[String, SVG] =
    Try {
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

  implicit final val svgCodec: Codec[SVG] = deriveConfiguredCodec
}
