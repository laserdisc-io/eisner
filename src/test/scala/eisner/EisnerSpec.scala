package eisner

import org.scalatest.{AsyncWordSpec, Matchers}
import scala.io.Source

final class DotSpec extends AsyncWordSpec with Matchers {
  import dot._
  import js._

  "A topology" when {
    "loaded from disk" must {
      "convert to a valid graph" in {
        val topologyTxt = Source.fromInputStream(getClass.getResourceAsStream("/topology.txt")).getLines.mkString("\n")
        val expectedGraph = Graph(
          List(
            Subgraph(
              "1",
              "Sub-\\ntopology: 1",
              Vector(
                Edge("KSTREAM-\\nSOURCE-\\n0000000006", "KSTREAM-\\nAGGREGATE-\\n0000000003"),
                Edge("KSTREAM-\\nAGGREGATE-\\n0000000003", "KTABLE-\\nTOSTREAM-\\n0000000007"),
                Edge("KTABLE-\\nTOSTREAM-\\n0000000007", "KSTREAM-\\nSINK-\\n0000000008")
              )
            ),
            Subgraph(
              "0",
              "Sub-\\ntopology: 0",
              Vector(
                Edge("KSTREAM-\\nSOURCE-\\n0000000000", "KSTREAM-\\nTRANSFORM-\\n0000000001"),
                Edge("KSTREAM-\\nTRANSFORM-\\n0000000001", "KSTREAM-\\nKEY-\\nSELECT-\\n0000000002"),
                Edge("KSTREAM-\\nKEY-\\nSELECT-\\n0000000002", "KSTREAM-\\nFILTER-\\n0000000005"),
                Edge("KSTREAM-\\nFILTER-\\n0000000005", "KSTREAM-\\nSINK-\\n0000000004")
              )
            )
          ),
          Vector(
            Edge("conversation-\\nmeta", "KSTREAM-\\nSOURCE-\\n0000000000"),
            Edge("KSTREAM-\\nTRANSFORM-\\n0000000001", "conversation-\\nmeta-\\nstate"),
            Edge("KSTREAM-\\nSINK-\\n0000000004", "count-\\nresolved-\\nrepartition"),
            Edge("count-\\nresolved-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000006"),
            Edge("KSTREAM-\\nAGGREGATE-\\n0000000003", "count-\\nresolved"),
            Edge("KSTREAM-\\nSINK-\\n0000000008", "streams-\\ncount-\\nresolved")
          ),
          Set(
            Topic("conversation-\\nmeta"),
            Topic("count-\\nresolved-\\nrepartition"),
            Topic("streams-\\ncount-\\nresolved")
          ),
          Set(
            Store("conversation-\\nmeta-\\nstate"),
            Store("count-\\nresolved")
          )
        )

        topologyTxt.dot shouldBe expectedGraph
      }
      "convert to a valid dot" in {
        val topologyTxt = Source.fromInputStream(getClass.getResourceAsStream("/topology.txt")).getLines.mkString("\n")
        val expectedDot = Source.fromInputStream(getClass.getResourceAsStream("/topology.dot")).getLines.mkString("\n")

        Writer[Graph].write(topologyTxt.dot, 0).mkString("\n").trim shouldBe expectedDot.trim
      }
      "convert to a valid svg" in {
        val topologyTxt = Source.fromInputStream(getClass.getResourceAsStream("/topology.txt")).getLines.mkString("\n")
        val expectedSvg = Source.fromInputStream(getClass.getResourceAsStream("/topology.svg")).getLines.mkString("\n")

        topologyTxt.dot.svg.map(_.trim shouldBe expectedSvg.trim)
      }
    }
  }
}
