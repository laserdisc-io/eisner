scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.2.2"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")
