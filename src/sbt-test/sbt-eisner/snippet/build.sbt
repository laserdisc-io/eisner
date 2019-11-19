scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.3.1",
  "org.slf4j"        % "slf4j-nop"     % "1.7.29"
)

enablePlugins(EisnerPlugin)
eisnerTopologiesSnippet := Some("""
  |import snippet._
  |
  |val settings = Settings(source = "topic a", destination = "topic b")
  |
  |List("snippet.EisnerTopology#myTopology" -> EisnerTopology.myTopology(settings))
  |""".stripMargin
)