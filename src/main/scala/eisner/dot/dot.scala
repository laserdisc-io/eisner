package eisner

package object dot {
  private[this] final val SubCat = "\\s*Sub-topology: ([0-9]*)".r
  private[this] final val Elems  = "\\s*(Source|Processor|Sink):\\s+(\\S+)\\s+\\((topics|stores):\\s\\[(.*)\\]\\)".r
  private[this] final val Elem   = "\\s*(Source|Processor|Sink):\\s+(\\S+)\\s+\\(topic:\\s(.*)\\)".r
  private[this] final val Arrow  = "\\s*-->\\s+(.*)".r

  private[this] final object Clean {
    final def unsafe(s: String): String = s.trim.replaceAll("-", "-\\\\n")
    final def unapply(s: String): Option[String] = {
      val res = unsafe(s)
      if (res.size > 0) Some(res) else None
    }
  }
  private[this] final object Links {
    final def unapplySeq(s: String): Option[Seq[String]] = {
      val res = s.split(',').flatMap(Clean.unapply).filter(_ != "none")
      if (res.size > 0) Some(res) else None
    }
  }

  implicit final class StringToDot(private val s: String) extends AnyVal {
    final def dot: Graph = {
      val (g, _) = s.split('\n').foldLeft(Graph.empty -> (None: Option[String])) {
        case ((Graph(sgs, es, ts, ss), _), sc @ SubCat(id)) =>
          Graph(Subgraph.empty(id, Clean.unsafe(sc)) :: sgs, es, ts, ss) -> None
        case ((Graph(sgs, es, ts, ss), _), Elems(_, Clean(n), "topics", Links(ls @ _*))) =>
          Graph(sgs, es ++ ls.map(l => Edge(l, n)), ts ++ ls.map(Topic(_)), ss) -> Some(n)
        case ((Graph(sgs, es, ts, ss), _), Elems(_, Clean(n), "stores", Links(ls @ _*))) =>
          Graph(sgs, es ++ ls.map(l => Edge(n, l)), ts, ss ++ ls.map(Store(_))) -> Some(n)
        case ((Graph(sgs, es, ts, ss), _), Elem(_, Clean(n), Links(l))) =>
          Graph(sgs, es :+ Edge(n, l), ts + Topic(l), ss) -> Some(n)
        case ((Graph(Subgraph(id, la, sges) :: sgs, es, ts, ss), Some(n)), Arrow(Links(l))) =>
          Graph(Subgraph(id, la, sges :+ Edge(n, l)) :: sgs, es, ts, ss) -> Some(l)
        case (acc, _) => acc
      }
      g
    }
  }
}
