scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.1.0"
)

enablePlugins(EisnerPlugin)
