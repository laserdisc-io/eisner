package eisner

final class LeftJoinTopologySpec extends BaseEisnerSpec(2) {
  override final val expectedDiGraph = DiGraph(
    List(
      SubGraph(
        "1",
        "Sub-\\ntopology: 1",
        Vector(
          Edge("KTABLE-\\nSOURCE-\\n0000000000", "KTABLE-\\nSOURCE-\\n0000000001")
        )
      ),
      SubGraph(
        "0",
        "Sub-\\ntopology: 0",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000002", "KSTREAM-\\nLEFTJOIN-\\n0000000003"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000003", "KSTREAM-\\nSINK-\\n0000000004")
        )
      )
    ),
    Vector(
      Edge("skus", "KSTREAM-\\nSOURCE-\\n0000000002"),
      Edge("KSTREAM-\\nSINK-\\n0000000004", "skus-\\nwith-\\ntaxcode"),
      Edge("taxcodes", "KTABLE-\\nSOURCE-\\n0000000000"),
      Edge("KTABLE-\\nSOURCE-\\n0000000001", "tax-\\ncodes-\\nstore")
    ),
    Set(
      Topic("skus"),
      Topic("skus-\\nwith-\\ntaxcode"),
      Topic("taxcodes")
    ),
    Set(
      Store("tax-\\ncodes-\\nstore")
    )
  )
}
