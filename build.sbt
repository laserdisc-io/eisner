val MajorVersionRegex = """(\d+)(?:.+)?""".r
val jdkMajorVersion: Int = System.getProperty("java.version") match {
  case MajorVersionRegex(version) => version.toInt
  case _                          => throw new RuntimeException("Couldn't parse major Java version")
}
def isJDK9Plus = jdkMajorVersion >= 9

inThisBuild {
  Seq(
    scalaVersion := "2.12.10",
    organization := "io.laserdisc",
    homepage := Some(url("https://github.com/laserdisc-io/eisner")),
    licenses += "MIT" -> url("http://opensource.org/licenses/MIT"),
    developers += Developer("sirocchj", "Julien Sirocchi", "julien.sirocchi@gmail.com", url("https://github.com/sirocchj"))
  )
}

val scalacSettings = Seq(
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
  )
)

val noPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val core = project
  .in(file("core"))
  .settings(scalacSettings)
  .settings(
    name := "eisner-core",
    libraryDependencies ++= Seq(
      "com.chuusai"            %% "shapeless"            % "2.3.3",
      "io.circe"               %% "circe-generic-extras" % "0.13.0",
      "io.circe"               %% "circe-parser"         % "0.13.0",
      "io.dylemma"             %% "xml-spac"             % "0.8",
      "net.arnx"               % "nashorn-promise"       % "0.1.3",
      "org.apache.kafka"       % "kafka-streams"         % "2.5.0",
      "org.scala-lang.modules" %% "scala-java8-compat"   % "0.9.1",
      "org.scalatest"          %% "scalatest"            % "3.1.1" % Test
    ),
    Test / parallelExecution := false
  )

lazy val plugin = project
  .in(file("plugin"))
  .dependsOn(core)
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-eisner",
    libraryDependencies += "org.clapper" %% "classutil" % "1.5.1",
    Compile / unmanagedSourceDirectories += (Compile / sourceDirectory).value / (if (isJDK9Plus) "scala-jdk9+" else "scala-jdk8-"),
    scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}"),
    scriptedBufferLog := false
  )

lazy val eisner = project
  .in(file("."))
  .aggregate(core, plugin)
  .settings(noPublishSettings)
  .settings(
    addCommandAlias("fmt", ";scalafmt;test:scalafmt;scalafmtSbt"),
    addCommandAlias("fmtCheck", ";scalafmtCheck;test:scalafmtCheck;scalafmtSbtCheck"),
    addCommandAlias("fullBuild", ";fmtCheck;clean;test;core/publishLocal;scripted")
  )
