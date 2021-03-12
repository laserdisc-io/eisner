package eisner

import io.dylemma.spac.xml._
import io.dylemma.spac.xml.XMLParser.{forMandatoryAttribute => at, forOptionalAttribute => optAt, forText => txt}

package object svg {
  private[svg] final val styleFun = (_: String).split(';').map(_.trim.split(':').map(_.trim).toList).foldLeft(("", "", 1.0d)) {
    case ((_, s, sw), "fill" :: f :: Nil)         => (f, s, sw)
    case ((f, _, sw), "stroke" :: s :: Nil)       => (f, s, sw)
    case ((f, s, _), "stroke-width" :: sw :: Nil) => (f, s, sw.toDouble)
    case (acc, _)                                 => acc
  }
  private[svg] final val dblAt = (s: String) => at(s).map(_.toDouble)

  private[svg] final val cx            = dblAt("cx")
  private[svg] final val cy            = dblAt("cy")
  private[svg] final val d             = at("d")
  private[svg] final val fill          = at("fill")
  private[svg] final val fillOpt       = optAt("fill")
  private[svg] final val `font-family` = at("font-family")
  private[svg] final val `font-size`   = dblAt("font-size")
  private[svg] final val height        = dblAt("height")
  private[svg] final val id            = at("id")
  private[svg] final val patternUnits  = at("patternUnits")
  private[svg] final val points        = at("points")
  private[svg] final val rx            = dblAt("rx")
  private[svg] final val ry            = dblAt("ry")
  private[svg] final val stroke        = at("stroke")
  private[svg] final val strokeWidth   = optAt("stroke-width").map(_.fold(1.0)(_.toDouble))
  private[svg] final val style         = at("style").map(styleFun)
  private[svg] final val `text-anchor` = at("text-anchor")
  private[svg] final val viewBoxOpt    = optAt("viewBox").map(_.flatMap(ViewBox.unapply))
  private[svg] final val width         = dblAt("width")
  private[svg] final val x             = dblAt("x")
  private[svg] final val y             = dblAt("y")

  private[svg] final val ellipse = (fill ~ stroke ~ strokeWidth ~ cx ~ cy ~ rx ~ ry).as(El.Ellipse)
  private[svg] final val path    = (fill ~ stroke ~ strokeWidth ~ d).as(El.Path.apply) orElse (style ~ d).as(El.Path.apply)
  private[svg] final val polygon = (fill ~ stroke ~ strokeWidth ~ points).as(El.Polygon)
  private[svg] final val text    = (`text-anchor` ~ x ~ y ~ `font-size` ~ `font-family` ~ fillOpt ~ txt).as(El.Text)

  private[svg] final val pattern = for {
    pc   <- (id ~ x ~ y ~ width ~ height ~ viewBoxOpt ~ patternUnits).asTuple.followedBy
    path <- XMLSplitter(* \ "path").map(path).parseFirst
  } yield Pattern(pc._1, pc._2, pc._3, pc._4, pc._5, pc._6, pc._7, path)

  private[svg] final val patterns  = XMLSplitter("svg" \ "defs").map(XMLSplitter(* \ "pattern").map(pattern).parseToList).parseFirst
  private[svg] final val transform = XMLSplitter("svg" \ "g").first.attr("transform")
  private[svg] final val title     = XMLSplitter("svg" \ "g" \ "text").map(text).parseFirst
  private[svg] final val elements = {
    XMLSplitter("svg" \ ** \ "ellipse").map(ellipse) parallel
      XMLSplitter("svg" \ ** \ "path").map(path) parallel
      XMLSplitter("svg" \ ** \ "polygon").map(polygon) parallel
      XMLSplitter("svg" \ ** \ "text").map(text)
  }.parseToList

  private[svg] final val svg = XMLSplitter(
    attr("width") & attr("height") & attr("viewBox").map(ViewBox.unapply(_).getOrElse(ViewBox.empty))
  ).map { case ((width, height), viewBox) =>
    for {
      patterns  <- patterns.followedBy
      transform <- transform.followedBy
      title     <- title.followedBy
      elements  <- elements
    } yield SVG(width, height, viewBox, transform, patterns, title :: elements)
  }.wrapSafe
    .map(_.toEither.left.map(e => SVGParserError(e.getLocalizedMessage())))
    .parseFirst

  final def toSVG(s: String): SVGParserError | SVG = svg.parse(s)
}
