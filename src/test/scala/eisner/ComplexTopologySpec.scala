package eisner

final class ComplexTopologySpec extends BaseEisnerSpec(3) {
  override final val expectedGraph = Graph(
    List(
      Subgraph(
        "1","Sub-\\ntopology: 1",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000020","KSTREAM-\\nAGGREGATE-\\n0000000002"),
          Edge("KSTREAM-\\nSOURCE-\\n0000000020","KSTREAM-\\nAGGREGATE-\\n0000000010"),
          Edge("KSTREAM-\\nAGGREGATE-\\n0000000002","KTABLE-\\nMAPVALUES-\\n0000000007"),
          Edge("KSTREAM-\\nAGGREGATE-\\n0000000010","KTABLE-\\nMAPVALUES-\\n0000000015"),
          Edge("KTABLE-\\nMAPVALUES-\\n0000000007","KTABLE-\\nTOSTREAM-\\n0000000008"),
          Edge("KTABLE-\\nMAPVALUES-\\n0000000015","KTABLE-\\nTOSTREAM-\\n0000000016"),
          Edge("KTABLE-\\nTOSTREAM-\\n0000000008","KSTREAM-\\nSINK-\\n0000000009"),
          Edge("KTABLE-\\nTOSTREAM-\\n0000000016","KSTREAM-\\nSINK-\\n0000000017")
        )
      ),
      Subgraph(
        "0",
        "Sub-\\ntopology: 0",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000000","KSTREAM-\\nKEY-\\nSELECT-\\n0000000001"),
          Edge("KSTREAM-\\nKEY-\\nSELECT-\\n0000000001","KSTREAM-\\nFILTER-\\n0000000019"),
          Edge("KSTREAM-\\nFILTER-\\n0000000019","KSTREAM-\\nSINK-\\n0000000018")
        )
      )
    ),
    Vector(
      Edge("streams-\\nplaintext-\\ninput","KSTREAM-\\nSOURCE-\\n0000000000"),
      Edge("KSTREAM-\\nSINK-\\n0000000018","count-\\nstore-\\nrepartition"),
      Edge("count-\\nstore-\\nrepartition","KSTREAM-\\nSOURCE-\\n0000000020"),
      Edge("KSTREAM-\\nAGGREGATE-\\n0000000002","count-\\nstore"),
      Edge("KSTREAM-\\nAGGREGATE-\\n0000000010","windowed-\\ncount-\\nstore"),
      Edge("KSTREAM-\\nSINK-\\n0000000009","count-\\ntopic"),
      Edge("KSTREAM-\\nSINK-\\n0000000017","windowed-\\ncount")
    ),
    Set(
      Topic("streams-\\nplaintext-\\ninput"),
      Topic("count-\\nstore-\\nrepartition"),
      Topic("count-\\ntopic"),
      Topic("windowed-\\ncount")
    ),
    Set(
      Store("count-\\nstore"),
      Store("windowed-\\ncount-\\nstore")
    )
  )
}
