package eisner
package svg

import scala.util.Try

final case class ViewBox(x: Double, y: Double, w: Double, h: Double) {
  override def toString: String = s"$x $y $w $h"
}
final object ViewBox {
  final val empty: ViewBox = ViewBox(0.0, 0.0, 0.0, 0.0)
  final def unapply(s: String): Option[ViewBox] =
    s.split(' ').toList match {
      case x :: y :: w :: h :: Nil => Try(ViewBox(x.toDouble, y.toDouble, w.toDouble, h.toDouble)).toOption
      case _                       => None
    }
}

final case class Pattern(
    id: String,
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    viewBox: Option[ViewBox],
    patternUnits: String,
    path: El.Path
)
object Pattern {
  implicit final val patternWriter: Writer[Pattern] = Writer.instance {
    case (Pattern(id, x, y, w, h, Some(vb), pu, p), i) =>
      s"""${i.tabs}<pattern id="$id" x="$x" y="$y" width="$w" height="$h" viewBox="$vb" patternUnits="$pu">""" ::
        p.write(i + 1) :::
        s"${i.tabs}</pattern>" ::
        Nil
    case (Pattern(id, x, y, w, h, None, pu, p), i) =>
      s"""${i.tabs}<pattern id="$id" x="$x" y="$y" width="$w" height="$h" patternUnits="$pu">""" ::
        p.write(i + 1) :::
        s"${i.tabs}</pattern>" ::
        Nil
  }
}

sealed trait El extends Product with Serializable
object El {
  final case class Ellipse(fill: String, stroke: String, strokeWidth: Double, cx: Double, cy: Double, rx: Double, ry: Double) extends El
  final case class Path(fill: String, stroke: String, strokeWidth: Double, d: String)                                         extends El
  final case class Polygon(fill: String, stroke: String, strokeWidth: Double, points: String)                                 extends El
  final case class Text(anchor: String, x: Double, y: Double, fontSize: Double, fontFamily: String, fill: Option[String], value: String)
      extends El

  object Path {
    final def apply(attributes: (String, String, Double), d: String): Path = Path(attributes._1, attributes._2, attributes._3, d)

    implicit final val pathWriter: Writer[Path] = Writer.instance { case (Path(f, s, sw, d), i) =>
      s"""${i.tabs}<path fill="$f" stroke="$s" stroke-width="$sw" d="$d"/>""" :: Nil
    }
  }

  implicit final val elWriter: Writer[El] = Writer.instance {
    case (Ellipse(f, s, sw, cx, cy, rx, ry), i) =>
      s"""${i.tabs}<ellipse fill="$f" stroke="$s" stroke-width="$sw" cx="$cx" cy="$cy" rx="$rx" ry="$ry"/>""" :: Nil
    case (p: Path, i)              => p.write(i)
    case (Polygon(f, s, sw, p), i) => s"""${i.tabs}<polygon fill="$f" stroke="$s" stroke-width="$sw" points="$p"/>""" :: Nil
    case (Text(a, x, y, fs, ff, Some(f), v), i) =>
      s"""${i.tabs}<text text-anchor="$a" x="$x" y="$y" font-size="$fs" font-family="$ff" fill="$f">$v</text>""" :: Nil
    case (Text(a, x, y, fs, ff, None, v), i) =>
      s"""${i.tabs}<text text-anchor="$a" x="$x" y="$y" font-size="$fs" font-family="$ff">$v</text>""" :: Nil
  }
}

final case class SVG(width: String, height: String, viewBox: ViewBox, transform: String, patterns: List[Pattern], elements: List[El])
final object SVG {
  implicit final val svgWriter: Writer[SVG] = Writer.instance {
    case (SVG(w, h, vb, t, Nil, es), _) =>
      s"""<?xml version="1.0" encoding="UTF-8" standalone="no"?>""" ::
        s"""<svg width="$w" height="$h" viewBox="$vb" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">""" ::
        s"""${1.tabs}<g transform="$t">""" ::
        es.write(2) :::
        s"${1.tabs}</g>" ::
        "</svg>" ::
        Nil
    case (SVG(w, h, vb, t, ps, es), _) =>
      s"""<?xml version="1.0" encoding="UTF-8" standalone="no"?>""" ::
        s"""<svg width="$w" height="$h" viewBox="$vb" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">""" ::
        s"${1.tabs}<defs>" ::
        ps.write(2) :::
        s"${1.tabs}</defs>" ::
        s"""${1.tabs}<g transform="$t">""" ::
        es.write(2) :::
        s"${1.tabs}</g>" ::
        "</svg>" ::
        Nil
  }
}
