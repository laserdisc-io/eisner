package eisner

final class SimpleTopologySpec extends BaseEisnerSpec(1) {
  override final val expectedDiGraph = DiGraph(
    List(
      SubGraph(
        "1",
        "Sub-\\ntopology: 1",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000006", "KSTREAM-\\nAGGREGATE-\\n0000000003"),
          Edge("KSTREAM-\\nAGGREGATE-\\n0000000003", "KTABLE-\\nTOSTREAM-\\n0000000007"),
          Edge("KTABLE-\\nTOSTREAM-\\n0000000007", "KSTREAM-\\nSINK-\\n0000000008")
        )
      ),
      SubGraph(
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
}
