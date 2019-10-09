package eisner

package object dot {
  private[this] final val SubCat = "\\s*(Sub-topology: ([0-9]*)).*".r
  private[this] final val Source = "\\s*Source:\\s+(\\S+)\\s+\\(topics:\\s\\[(.*)\\]\\)".r
  private[this] final val Proc   = "\\s*Processor:\\s+(\\S+)\\s+\\(stores:\\s\\[(.*)\\]\\)".r
  private[this] final val Sink   = "\\s*Sink:\\s+(\\S+)\\s+\\(topic:\\s(.*)\\)".r
  private[this] final val Arrow  = "\\s*-->\\s+(.*)".r

  private[this] final object Clean {
    final def unapply(s: String): Option[String] = {
      val res = s.trim.replaceAll("-", "-\\\\n")
      if (res.size > 0) Some(res) else None
    }
  }
  private[this] final object Links {
    final def unapplySeq(s: String): Option[Seq[String]] = Some(s.split(',').flatMap(Clean.unapply).filter(_ != "none"))
  }

  final def toDot(s: String): Graph = {
    val (g, _) = s.split('\n').foldLeft(Graph.empty -> (None: Option[String])) {
      case ((Graph(sgs, es, ts, ss), maybeN), SubCat(Clean(sc), id)) =>
        Graph(Subgraph.empty(id, sc) :: sgs, es, ts, ss) -> maybeN
      case ((Graph(sgs, es, ts, ss), _), Source(Clean(n), Links(ls @ _*))) =>
        Graph(sgs, es ++ ls.map(l => Edge(l, n)), ts ++ ls.map(Topic(_)), ss) -> Some(n)
      case ((Graph(sgs, es, ts, ss), _), Proc(Clean(n), Links(ls @ _*))) =>
        Graph(sgs, es ++ ls.map(l => Edge(n, l)), ts, ss ++ ls.map(Store(_))) -> Some(n)
      case ((Graph(sgs, es, ts, ss), _), Sink(Clean(n), Links(l))) =>
        Graph(sgs, es :+ Edge(n, l), ts + Topic(l), ss) -> Some(n)
      case ((Graph(Subgraph(id, la, sges) :: sgs, es, ts, ss), Some(n)), Arrow(Links(ls @ _*))) =>
        Graph(Subgraph(id, la, sges ++ ls.map(l => Edge(n, l))) :: sgs, es, ts, ss) -> Some(n)
      case (acc, _) =>
        acc
    }
    g
  }
}
