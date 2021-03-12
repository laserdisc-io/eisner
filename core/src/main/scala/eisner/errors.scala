package eisner

sealed abstract class EisnerError(val msg: String)                   extends RuntimeException(msg, null, true, false)
final case class TopologyParserError(override final val msg: String) extends EisnerError(msg)
final case class GraphvizError(override final val msg: String)       extends EisnerError(msg)
final case class SVGParserError(override final val msg: String)      extends EisnerError(msg)
