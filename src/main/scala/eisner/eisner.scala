import scala.concurrent.Future

package object eisner {
  final type Graph = dot.Graph
  final type Node  = xml.Node

  final val Graph = dot.Graph
  final val Subgraph = dot.Subgraph
  final val Edge = dot.Edge
  final val Topic = dot.Topic
  final val Store = dot.Store

  implicit final class IntOps(private val i: Int) extends AnyVal {
    final def tabs: String = "\t" * i
  }
  implicit final class StringOps(private val s: String) extends AnyVal {
    final def dot: Graph = eisner.dot.toDot(s)
  }
  implicit final class GraphOps(private val g: Graph) extends AnyVal {
    final def svg: Future[Node] = eisner.js.toSVG(Writer[Graph].write(g, 0).mkString("\n"))
  }
}