scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.8.0"
)

enablePlugins(EisnerPlugin)
eisnerTopologiesSnippet := Some("""
  |import snippet._
  |
  |val settings = Settings(source = "topic a", destination = "topic b")
  |
  |List("snippet.EisnerTopology#myTopology" -> EisnerTopology.myTopology(settings))
  |""".stripMargin)
