scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.7.0"
)

enablePlugins(EisnerPlugin)