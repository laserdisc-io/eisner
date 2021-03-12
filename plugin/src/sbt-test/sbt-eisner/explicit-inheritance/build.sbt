scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.7.0"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")