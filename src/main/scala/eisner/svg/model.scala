package eisner
package svg

import io.circe.Codec
import io.circe.generic.extras.semiauto._

import scala.util.Try

final case class ViewBox(x: Double, y: Double, w: Double, h: Double) {
  override def toString: String = s"$x $y $w $h"
}
final object ViewBox {
  final def unapply(s: String): Option[ViewBox] = s.split(' ').toList match {
    case x :: y :: w :: h :: Nil => Try(ViewBox(x.dbl, y.dbl, w.dbl, h.dbl)).toOption
    case _                       => None
  }

  implicit final val viewBoxCodec: Codec[ViewBox] = deriveConfiguredCodec
}

final case class Pattern(id: String, x: Double, y: Double, width: Double, height: Double, patternUnits: String, path: El.Path)
object Pattern {
  implicit final val patternCodec: Codec[Pattern] = deriveConfiguredCodec
  implicit final val patternWriter: Writer[Pattern] = Writer.instance {
    case (Pattern(id, x, y, w, h, pu, p), i) =>
      s"""${i.tabs}<pattern id="$id" x="$x" y="$y" width="$w" height="$h" patternUnits="$pu">""" ::
        Writer[El.Path].write(p, i + 1) :::
        s"${i.tabs}</pattern>" ::
        Nil
  }
}

sealed trait El extends Product with Serializable
object El {
  final case class Ellipse(cx: Double, cy: Double, rx: Double, ry: Double, stroke: String, strokeWidth: Double, fill: String)    extends El
  final case class Path(d: String, stroke: String, strokeWidth: Double, fill: String)                                            extends El
  final case class Polygon(points: String, stroke: String, strokeWidth: Double, fill: String)                                    extends El
  final case class Text(x: Double, y: Double, anchor: String, fontFamily: String, fontSize: Double, fill: String, value: String) extends El

  object Path {
    implicit final val pathCodec: Codec[Path] = deriveConfiguredCodec
    implicit final val pathWriter: Writer[Path] = Writer.instance {
      case (Path(d, s, sw, f), i) => s"""${i.tabs}<path d="$d" stroke="$s" stroke-width="$sw" fill="$f"/>""" :: Nil
    }
  }

  implicit final val elCodec: Codec[El] = deriveConfiguredCodec
  implicit final val elWriter: Writer[El] = Writer.instancePF {
    case (p: Path, i) => Writer[Path].write(p, i)
    case (Text(x, y, a, ff, fs, f, v), i) =>
      s"""${i.tabs}<text text-anchor="$a" x="$x" y="$y" font-family="$ff" font-size="$fs" fill="$f">$v</text>""" :: Nil
  }
}

final case class SVG(width: String, height: String, viewBox: ViewBox, transform: String, patterns: List[Pattern], elements: List[El])
final object SVG {

  implicit final val svgCodec: Codec[SVG] = deriveConfiguredCodec
  implicit final val svgWriter: Writer[SVG] = Writer.instance {
    case (SVG(w, h, vb, t, ps, es), _) =>
      s"""<svg width="$w" height="$h" viewBox="$vb" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">""" ::
        s"${1.tabs}<defs>" ::
        Writer[List[Pattern]].write(ps, 2) :::
        s"${1.tabs}</defs>" ::
        s"""${1.tabs}<g transform="$t">""" ::
        Writer[List[El]].write(es, 2) :::
        s"${1.tabs}</g>" ::
        "</svg>" ::
        Nil
  }
}
