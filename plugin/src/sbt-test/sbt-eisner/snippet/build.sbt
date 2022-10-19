scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.2.3"
)

enablePlugins(EisnerPlugin)
eisnerTopologiesSnippet := Some("""
  |import snippet._
  |
  |val settings = Settings(source = "topic a", destination = "topic b")
  |
  |List("snippet.EisnerTopology#myTopology" -> EisnerTopology.myTopology(settings))
  |""".stripMargin)
