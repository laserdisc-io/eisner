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

  final def toDot(s: String): DiGraph = {
    val (g, _) = s.split('\n').foldLeft(DiGraph.empty -> (None: Option[String])) {
      case ((DiGraph(sgs, es, ts, ss), maybeN), SubTopology(Clean(sc), id)) =>
        DiGraph(Subgraph.empty(id, sc) :: sgs, es, ts, ss) -> maybeN
      case ((DiGraph(sgs, es, ts, ss), _), Source(Clean(n), Links(ls @ _*))) =>
        DiGraph(sgs, es ++ ls.map(Edge(_, n)), ts ++ ls.map(Topic(_)), ss) -> Some(n)
      case ((DiGraph(sgs, es, ts, ss), _), Processor(Clean(n), Links(ls @ _*))) =>
        DiGraph(sgs, es ++ ls.map(Edge(n, _)), ts, ss ++ ls.map(Store(_))) -> Some(n)
      case ((DiGraph(sgs, es, ts, ss), _), Sink(Clean(n), Links(l))) =>
        DiGraph(sgs, es :+ Edge(n, l), ts + Topic(l), ss) -> Some(n)
      case ((DiGraph(Subgraph(id, la, sges) :: sgs, es, ts, ss), Some(n)), RightArrow(Links(ls @ _*))) =>
        DiGraph(Subgraph(id, la, sges ++ ls.map(Edge(n, _))) :: sgs, es, ts, ss) -> Some(n)
      case (acc, _) =>
        acc
    }
    g
  }
}
