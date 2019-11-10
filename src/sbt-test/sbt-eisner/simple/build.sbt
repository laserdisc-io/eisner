scalaVersion := "2.12.10"
name := "simple"
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.3.1",
  "org.slf4j"        % "slf4j-nop"     % "1.7.29"
)

enablePlugins(EisnerPlugin)