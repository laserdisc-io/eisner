scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.0.0"
)

enablePlugins(EisnerPlugin)
