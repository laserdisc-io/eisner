scalaVersion := "2.13.7"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.8.1"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")
