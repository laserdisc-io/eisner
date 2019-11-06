package eisner

import org.scalatest.{AsyncWordSpec, EitherValues, Matchers}
import scala.io.Source

abstract class BaseEisnerSpec(i: Int) extends AsyncWordSpec with Matchers with EitherValues {
  def expectedDiGraph: DiGraph

  lazy val txt = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.txt")).getLines.mkString("\n")
  lazy val dot = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.dot")).getLines.mkString("\n")
  lazy val svg = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.svg")).getLines.mkString("\n")

  s"Topology #$i" when {
    "loaded from disk" must {
      "convert to a valid graph" in {
        txt.toDiGraph.right.value shouldBe expectedDiGraph
      }
      "convert to a valid dot" in {
        txt.toDot.right.value shouldBe dot
      }
      "convert to a valid svg" in {
        txt.toSVG.map(_.trim shouldBe svg)
      }
    }
  }
}
