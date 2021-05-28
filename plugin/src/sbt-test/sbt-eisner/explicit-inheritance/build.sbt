scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.8.0"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")