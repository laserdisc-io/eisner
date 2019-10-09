import scala.concurrent.Future

package object eisner {
  final type DiGraph = dot.DiGraph
  final type Node    = xml.Node

  final val DiGraph  = dot.DiGraph
  final val Subgraph = dot.Subgraph
  final val Edge     = dot.Edge
  final val Topic    = dot.Topic
  final val Store    = dot.Store

  implicit final class IntOps(private val i: Int) extends AnyVal {
    final def tabs: String = "\t" * i
  }
  implicit final class StringOps(private val s: String) extends AnyVal {
    final def dot: DiGraph      = eisner.dot.toDot(s)
    final def dotString: String = dot.dotString
  }
  implicit final class DotOps(private val dg: DiGraph) extends AnyVal {
    final def dotString: String = Writer[DiGraph].write(dg, 0).mkString("\n")
    final def svg: Future[Node] = eisner.js.toSVG(dotString)
  }
}
