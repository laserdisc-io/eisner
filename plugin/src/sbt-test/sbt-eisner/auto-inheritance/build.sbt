scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.0.0"
)

enablePlugins(EisnerPlugin)
