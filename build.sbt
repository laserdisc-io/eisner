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
      "io.circe"               %% "circe-generic-extras" % "0.12.2",
      "io.circe"               %% "circe-parser"         % "0.12.2",
      "net.arnx"               % "nashorn-promise"       % "0.1.2",
      "org.clapper"            %% "classutil"            % "1.5.1",
      "org.scala-lang.modules" %% "scala-java8-compat"   % "0.9.0",
      "org.scalatest"          %% "scalatest"            % "3.0.8" % Test
    ),
    scalacOptions := Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-explaintypes",
      "-Yrangepos",
      "-feature",
      "-Xfuture",
      "-Ypartial-unification",
      "-language:higherKinds",
      "-language:existentials",
      "-unchecked",
      "-Yno-adapted-args",
      "-Xlint:constant",
      "-Xlint:_,-type-parameter-shadow",
      "-Xsource:2.12",
      "-Ywarn-dead-code",
      "-Ywarn-extra-implicit",
      "-Ywarn-inaccessible",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:_,imports",
      "-Ywarn-value-discard",
      "-Xfatal-warnings",
      "-opt-inline-from:<source>",
      "-opt:l:inline",
      "-opt-warnings"
    ),
    scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}"),
    scriptedBufferLog := false,
    Test / parallelExecution := false
  )
