package eisner

import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

abstract class BaseEisnerSpec(i: Int) extends AnyWordSpec with Matchers with EitherValues {
  def expectedDiGraph: DiGraph

  val config = Config("lightgrey", "black", "black")

  lazy val txt = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.txt")).getLines.mkString("\n").trim
  lazy val dot = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.dot")).getLines.mkString("\n").trim
  lazy val svg = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.svg")).getLines.mkString("\n").trim

  s"Topology #$i" when {
    "loaded from disk" must {
      "convert to a valid graph" in {
        txt.toDiGraph(config).value shouldBe expectedDiGraph
      }
      "convert to a valid dot" in {
        txt.toDot(config).value shouldBe dot
      }
      "convert to a valid svg" in {
        txt.toSVG(config).value.trim shouldBe svg
      }
    }
  }
}
