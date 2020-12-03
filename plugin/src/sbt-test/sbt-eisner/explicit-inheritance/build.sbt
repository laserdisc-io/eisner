scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.6.0",
  "org.slf4j"        % "slf4j-nop"     % "1.7.29"
)

enablePlugins(EisnerPlugin)
eisnerTopologies := Seq("explicit.inheritance.EisnerTopology")