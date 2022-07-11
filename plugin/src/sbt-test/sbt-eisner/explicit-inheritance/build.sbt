scalaVersion := "2.12.16"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.2.0"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")
