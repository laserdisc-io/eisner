scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.8.0"
)

enablePlugins(EisnerPlugin)
