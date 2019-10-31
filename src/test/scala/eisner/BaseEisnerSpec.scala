package eisner

import org.scalatest.{AsyncWordSpec, Matchers}
import scala.io.Source
import scala.xml.XML

abstract class BaseEisnerSpec(i: Int) extends AsyncWordSpec with Matchers {

  def expectedDiGraph: DiGraph

  lazy val txt = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.txt")).getLines.mkString("\n")
  lazy val dot = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.dot")).getLines.mkString("\n")
  lazy val svg = Source.fromInputStream(getClass.getResourceAsStream(s"/topology$i.svg")).getLines.mkString("\n")

  s"Topology #$i" when {
    "loaded from disk" must {
      "convert to a valid graph" in {
        txt.dot shouldBe expectedDiGraph
      }
      "convert to a valid dot" in {
        txt.dotString shouldBe dot
      }
      "convert to a valid svg" in {
        js.viz(txt.dotString).map(_.trim shouldBe svg)
      }
    }
  }
}
