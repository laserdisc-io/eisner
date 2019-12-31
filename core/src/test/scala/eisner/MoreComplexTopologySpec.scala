package eisner

final class MoreComplexTopologySpec extends BaseEisnerSpec(4) {
  override final val expectedDiGraph = DiGraph(
    List(
      SubGraph(
        "4",
        "Sub-\\ntopology: 4",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000049", "KSTREAM-\\nREDUCE-\\n0000000046"),
          Edge("KSTREAM-\\nREDUCE-\\n0000000046", "KTABLE-\\nTOSTREAM-\\n0000000050"),
          Edge("KTABLE-\\nTOSTREAM-\\n0000000050", "KSTREAM-\\nSINK-\\n0000000051")
        ),
        "lightgrey"
      ),
      SubGraph(
        "3",
        "Sub-\\ntopology: 3",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000021", "KSTREAM-\\nLEFTJOIN-\\n0000000022"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000022", "KSTREAM-\\nFLATMAPVALUES-\\n0000000023"),
          Edge("KSTREAM-\\nFLATMAPVALUES-\\n0000000023", "KSTREAM-\\nKEY-\\nSELECT-\\n0000000024"),
          Edge("KSTREAM-\\nFLATMAPVALUES-\\n0000000023", "KSTREAM-\\nLEFTJOIN-\\n0000000030"),
          Edge("KSTREAM-\\nSOURCE-\\n0000000033", "KSTREAM-\\nLEFTJOIN-\\n0000000034"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000034", "KSTREAM-\\nFLATMAPVALUES-\\n0000000035"),
          Edge("KSTREAM-\\nSOURCE-\\n0000000039", "KSTREAM-\\nLEFTJOIN-\\n0000000040"),
          Edge("KSTREAM-\\nFLATMAPVALUES-\\n0000000035", "KSTREAM-\\nLEFTJOIN-\\n0000000036"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000040", "KSTREAM-\\nFLATMAPVALUES-\\n0000000041"),
          Edge("KSTREAM-\\nFLATMAPVALUES-\\n0000000041", "KSTREAM-\\nKEY-\\nSELECT-\\n0000000042"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000030", "KSTREAM-\\nMERGE-\\n0000000043"),
          Edge("KSTREAM-\\nLEFTJOIN-\\n0000000036", "KSTREAM-\\nMERGE-\\n0000000043"),
          Edge("KSTREAM-\\nKEY-\\nSELECT-\\n0000000042", "KSTREAM-\\nMERGE-\\n0000000044"),
          Edge("KSTREAM-\\nMERGE-\\n0000000043", "KSTREAM-\\nMERGE-\\n0000000044"),
          Edge("KSTREAM-\\nKEY-\\nSELECT-\\n0000000024", "KSTREAM-\\nFILTER-\\n0000000028"),
          Edge("KSTREAM-\\nMERGE-\\n0000000044", "KSTREAM-\\nFILTER-\\n0000000048"),
          Edge("KSTREAM-\\nFILTER-\\n0000000028", "KSTREAM-\\nSINK-\\n0000000027"),
          Edge("KSTREAM-\\nFILTER-\\n0000000048", "KSTREAM-\\nSINK-\\n0000000047"),
          Edge("KSTREAM-\\nSOURCE-\\n0000000013", "KSTREAM-\\nREDUCE-\\n0000000010"),
          Edge("KSTREAM-\\nSOURCE-\\n0000000018", "KSTREAM-\\nREDUCE-\\n0000000015"),
          Edge("KSTREAM-\\nSOURCE-\\n0000000029", "KSTREAM-\\nREDUCE-\\n0000000026")
        ),
        "lightgrey"
      ),
      SubGraph(
        "2",
        "Sub-\\ntopology: 2",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000004", "KSTREAM-\\nFLATMAP-\\n0000000005"),
          Edge("KSTREAM-\\nFLATMAP-\\n0000000005", "KSTREAM-\\nFILTER-\\n0000000038"),
          Edge("KSTREAM-\\nFLATMAP-\\n0000000005", "KSTREAM-\\nSINK-\\n0000000006"),
          Edge("KSTREAM-\\nFILTER-\\n0000000038", "KSTREAM-\\nSINK-\\n0000000037")
        ),
        "lightgrey"
      ),
      SubGraph(
        "1",
        "Sub-\\ntopology: 1",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000002", "KSTREAM-\\nFLATMAP-\\n0000000003"),
          Edge("KSTREAM-\\nFLATMAP-\\n0000000003", "KSTREAM-\\nFILTER-\\n0000000017"),
          Edge("KSTREAM-\\nFLATMAP-\\n0000000003", "KSTREAM-\\nFILTER-\\n0000000032"),
          Edge("KSTREAM-\\nFILTER-\\n0000000017", "KSTREAM-\\nSINK-\\n0000000016"),
          Edge("KSTREAM-\\nFILTER-\\n0000000032", "KSTREAM-\\nSINK-\\n0000000031")
        ),
        "lightgrey"
      ),
      SubGraph(
        "0",
        "Sub-\\ntopology: 0",
        Vector(
          Edge("KSTREAM-\\nSOURCE-\\n0000000000", "KSTREAM-\\nFLATMAP-\\n0000000001"),
          Edge("KSTREAM-\\nFLATMAP-\\n0000000001", "KSTREAM-\\nFILTER-\\n0000000012"),
          Edge("KSTREAM-\\nFLATMAP-\\n0000000001", "KSTREAM-\\nFILTER-\\n0000000020"),
          Edge("KSTREAM-\\nFILTER-\\n0000000012", "KSTREAM-\\nSINK-\\n0000000011"),
          Edge("KSTREAM-\\nFILTER-\\n0000000020", "KSTREAM-\\nSINK-\\n0000000019")
        ),
        "lightgrey"
      )
    ),
    Vector(
      Edge("srcItemsTopic", "KSTREAM-\\nSOURCE-\\n0000000000"),
      Edge("KSTREAM-\\nSINK-\\n0000000011", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000009-\\nrepartition"),
      Edge("KSTREAM-\\nSINK-\\n0000000019", "KSTREAM-\\nFLATMAP-\\n0000000001-\\nrepartition"),
      Edge("srcFeeTopic", "KSTREAM-\\nSOURCE-\\n0000000002"),
      Edge("KSTREAM-\\nSINK-\\n0000000016", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000014-\\nrepartition"),
      Edge("KSTREAM-\\nSINK-\\n0000000031", "KSTREAM-\\nFLATMAP-\\n0000000003-\\nrepartition"),
      Edge("srcDeptTopic", "KSTREAM-\\nSOURCE-\\n0000000004"),
      Edge("KSTREAM-\\nSINK-\\n0000000006", "tblDeptTopic"),
      Edge("KSTREAM-\\nSINK-\\n0000000037", "KSTREAM-\\nFLATMAP-\\n0000000005-\\nrepartition"),
      Edge("KSTREAM-\\nFLATMAP-\\n0000000001-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000021"),
      Edge("KSTREAM-\\nLEFTJOIN-\\n0000000022", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000014"),
      Edge("KSTREAM-\\nFLATMAP-\\n0000000003-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000033"),
      Edge("KSTREAM-\\nLEFTJOIN-\\n0000000034", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000009"),
      Edge("KSTREAM-\\nFLATMAP-\\n0000000005-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000039"),
      Edge("KSTREAM-\\nLEFTJOIN-\\n0000000040", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000025"),
      Edge("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000009-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000013"),
      Edge("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000014-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000018"),
      Edge("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000025-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000029"),
      Edge("KSTREAM-\\nREDUCE-\\n0000000010", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000009"),
      Edge("KSTREAM-\\nREDUCE-\\n0000000015", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000014"),
      Edge("KSTREAM-\\nREDUCE-\\n0000000026", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000025"),
      Edge("KSTREAM-\\nSINK-\\n0000000027", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000025-\\nrepartition"),
      Edge("KSTREAM-\\nSINK-\\n0000000047", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000045-\\nrepartition"),
      Edge("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000045-\\nrepartition", "KSTREAM-\\nSOURCE-\\n0000000049"),
      Edge("KSTREAM-\\nREDUCE-\\n0000000046", "KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000045"),
      Edge("KSTREAM-\\nSINK-\\n0000000051", "snkItemsTopic")
    ),
    Set(
      Topic("KSTREAM-\\nFLATMAP-\\n0000000003-\\nrepartition", "black"),
      Topic("KSTREAM-\\nFLATMAP-\\n0000000005-\\nrepartition", "black"),
      Topic("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000025-\\nrepartition", "black"),
      Topic("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000014-\\nrepartition", "black"),
      Topic("tblDeptTopic", "black"),
      Topic("srcFeeTopic", "black"),
      Topic("srcDeptTopic", "black"),
      Topic("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000045-\\nrepartition", "black"),
      Topic("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000009-\\nrepartition", "black"),
      Topic("snkItemsTopic", "black"),
      Topic("KSTREAM-\\nFLATMAP-\\n0000000001-\\nrepartition", "black"),
      Topic("srcItemsTopic", "black")
    ),
    Set(
      Store("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000014", "black"),
      Store("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000009", "black"),
      Store("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000025", "black"),
      Store("KSTREAM-\\nREDUCE-\\nSTATE-\\nSTORE-\\n0000000045", "black")
    )
  )
}
