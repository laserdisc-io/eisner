package eisner

import org.scalatest.EitherValues
import scala.io.Source
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

abstract class BaseEisnerSpec(i: Int) extends AsyncWordSpec with Matchers with EitherValues {
  def expectedDiGraph: DiGraph

  val config = Config("lightgrey", "black", "black")

  lazy val txt = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.txt")).getLines.mkString("\n")
  lazy val dot = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.dot")).getLines.mkString("\n")
  lazy val svg = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.svg")).getLines.mkString("\n")

  s"Topology #$i" when {
    "loaded from disk" must {
      "convert to a valid graph" in {
        txt.toDiGraph(config).value shouldBe expectedDiGraph
      }
      "convert to a valid dot" in {
        txt.toDot(config).value shouldBe dot
      }
      "convert to a valid svg" in {
        txt.toSVG(config).map(_.trim shouldBe svg)
      }
    }
  }
}
