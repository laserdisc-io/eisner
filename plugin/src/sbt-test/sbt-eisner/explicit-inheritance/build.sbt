scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "3.1.0"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")
