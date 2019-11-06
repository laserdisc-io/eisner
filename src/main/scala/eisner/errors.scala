package eisner

sealed abstract class EisnerError(msg: String)    extends RuntimeException(msg, null, true, false)
final case class TopologyParserError(msg: String) extends EisnerError(msg)
final case class SVGParserError(msg: String)      extends EisnerError(msg)
