package eisner

final class LeftJoinTopologySpec extends BaseEisnerSpec(2) {
  override final val expectedDiGraph = DiGraph(
    List(
      SubGraph(
        "1",
        "Sub-\\ntopology: 1",
        Vector(
          Edge("KTABLE-\\nSOURCE-\\n0000000000", "KTABLE-\\nSOURCE-\\n0000000001")
        ),
        "lightgrey"
      ),
      SubGraph(
        "0",
        "Sub-\\ntopology: 0",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000002", "KSTREAM-\\nLEFTJOIN-\\n0000000003"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000003", "KSTREAM-\\nSINK-\\n0000000004")
        ),
        "lightgrey"
      )
    ),
    Vector(
      Edge("skus", "KSTREAM-\\nSOURCE-\\n0000000002"),
      Edge("KSTREAM-\\nSINK-\\n0000000004", "skus-\\nwith-\\ntaxcode"),
      Edge("taxcodes", "KTABLE-\\nSOURCE-\\n0000000000"),
      Edge("KTABLE-\\nSOURCE-\\n0000000001", "tax-\\ncodes-\\nstore")
    ),
    Set(
      Topic("skus", "black"),
      Topic("skus-\\nwith-\\ntaxcode", "black"),
      Topic("taxcodes", "black")
    ),
    Set(
      Store("tax-\\ncodes-\\nstore", "black")
    )
  )
}
