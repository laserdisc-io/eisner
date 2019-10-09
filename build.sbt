inThisBuild {
  Seq(
    organization := "io.laserdisc",
    homepage := Some(url("https://github.com/laserdisc-io/eisner")),
    licenses += "MIT" -> url("http://opensource.org/licenses/MIT"),
    developers += Developer("sirocchj", "Julien Sirocchi", "julien.sirocchi@gmail.com", url("https://github.com/sirocchj"))
  )
}

lazy val eisner = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "eisner",
    libraryDependencies ++= Seq(
      "io.circe"               %% "circe-core"         % "0.12.1",
      "net.arnx"               % "nashorn-promise"     % "0.1.1",
      "org.clapper"            %% "classutil"          % "1.5.1",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0",
      "org.scalatest"          %% "scalatest"          % "3.0.8" % Test
    ),
    scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}"),
    scriptedBufferLog := false
  )
