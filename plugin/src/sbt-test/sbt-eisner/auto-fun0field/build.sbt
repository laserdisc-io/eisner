scalaVersion := "2.12.20"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.2.2"
)

enablePlugins(EisnerPlugin)
