package eisner
package js

import io.circe.{Encoder, Json}
import io.circe.syntax._

sealed trait Elem extends Product with Serializable
object Elem {
  final case class Path(d: String, options: Options)                                         extends Elem
  final case class Ellipse(cx: Double, cy: Double, rx: Double, ry: Double, options: Options) extends Elem
  final case class Polygon(points: String, options: Options)                                 extends Elem

  final case class Options(fill: Option[String], stroke: Option[String])
  final object Options {
    private[this] final val none: Json = "none".asJson
    implicit final val optionsEncoder: Encoder[Options] = Encoder.instance {
      case Options(Some(fill), Some(stroke)) => Json.obj("fill" -> fill.asJson, "stroke" -> stroke.asJson)
      case Options(Some(fill), None)         => Json.obj("fill" -> fill.asJson, "stroke" -> none)
      case Options(None, Some(stroke))       => Json.obj("fill" -> none, "stroke"        -> stroke.asJson)
      case Options(None, None)               => Json.obj("fill" -> none, "stroke"        -> none)
    }
  }

  implicit final val elemEncoder: Encoder[Elem] = Encoder.instance {
    case Path(d, o) => Json.obj("type" -> "path".asJson, "paths" -> d.asJson, "options" -> o.asJson)
    case Ellipse(cx, cy, rx, ry, o) =>
      Json.obj("type" -> "ellipse".asJson, "cx" -> cx.asJson, "cy" -> cy.asJson, "rx" -> rx.asJson, "ry" -> ry.asJson, "options" -> o.asJson)
    case Polygon(p, o) => Json.obj("type" -> "path".asJson, "paths" -> s"M${p}Z".asJson, "options" -> o.asJson)
  }
}

final case class G(width: Double, height: Double, elements: List[Elem])
final object G {
  implicit final val gEncoder: Encoder[G] = Encoder.instance {
    case G(w, h, es) => Json.obj("width" -> w.asJson, "height" -> h.asJson, "elements" -> es.asJson)
  }
}
