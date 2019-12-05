package eisner

package object dot {
  private[this] final val SubTopology = "\\s*(Sub-topology: ([0-9]*)).*".r
  private[this] final val Source      = "\\s*Source:\\s+(\\S+)\\s+\\(topics:\\s\\[(.*)\\]\\)".r
  private[this] final val Processor   = "\\s*Processor:\\s+(\\S+)\\s+\\(stores:\\s\\[(.*)\\]\\)".r
  private[this] final val Sink        = "\\s*Sink:\\s+(\\S+)\\s+\\(topic:\\s(.*)\\)".r
  private[this] final val RightArrow  = "\\s*-->\\s+(.*)".r

  private[this] final object Clean {
    final def unapply(s: String): Option[String] = {
      val res = s.trim.replaceAll("-", "-\\\\n")
      if (res.size > 0) Some(res) else None
    }
  }
  private[this] final object Links {
    final def unapplySeq(s: String): Option[Seq[String]] = Some(s.split(',').flatMap(Clean.unapply).filter(_ != "none"))
  }

  final def toDot(c: Config, s: String): TopologyParserError | DiGraph = {
    if (!s.startsWith("Topolog"))
      Left(TopologyParserError(s"Supplied string does not appear to be a valid topology (${s.substring(0, 10)}...)"))
    else {
      val (g, _) = s.split('\n').foldLeft(DiGraph.empty -> (None: Option[String])) {
        case ((DiGraph(sgs, es, ts, ss), maybeN), SubTopology(Clean(sc), id)) =>
          DiGraph(SubGraph.empty(id, sc, c.subgraphColor) :: sgs, es, ts, ss) -> maybeN
        case ((DiGraph(sgs, es, ts, ss), _), Source(Clean(n), Links(ls @ _*))) =>
          DiGraph(sgs, es ++ ls.map(Edge(_, n)), ts ++ ls.map(Topic(_, c.topicColor)), ss) -> Some(n)
        case ((DiGraph(sgs, es, ts, ss), _), Processor(Clean(n), Links(ls @ _*))) =>
          DiGraph(sgs, es ++ ls.map(Edge(n, _)), ts, ss ++ ls.map(Store(_, c.storeColor))) -> Some(n)
        case ((DiGraph(sgs, es, ts, ss), _), Sink(Clean(n), Links(l))) =>
          DiGraph(sgs, es :+ Edge(n, l), ts + Topic(l, c.topicColor), ss) -> Some(n)
        case ((DiGraph(SubGraph(id, la, sges, color) :: sgs, es, ts, ss), Some(n)), RightArrow(Links(ls @ _*))) =>
          DiGraph(SubGraph(id, la, sges ++ ls.map(Edge(n, _)), color) :: sgs, es, ts, ss) -> Some(n)
        case (acc, _) =>
          acc
      }
      if (g != DiGraph.empty)
        Right(g)
      else
        Left(TopologyParserError(s"Supplied topology does not contain valid subtopologies (${s.substring(0, 10)}...)"))
    }
  }
}
