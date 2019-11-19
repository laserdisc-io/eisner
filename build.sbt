val `scala 2.12` = "2.12.10"

val MajorVersionRegex = """(\d+)(?:.+)?""".r
val jdkMajorVersion: Int = System.getProperty("java.version") match {
  case MajorVersionRegex(version) => version.toInt
  case _                          => throw new RuntimeException("Couldn't parse major Java version")
}
def isJDK9Plus = jdkMajorVersion >= 9

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
    name := "sbt-eisner",
    Compile / unmanagedSourceDirectories += (Compile / sourceDirectory).value / (if (isJDK9Plus) "scala-jdk9+" else "scala-jdk8-"),
    scalaVersion := `scala 2.12`,
    libraryDependencies ++= Seq(
      "com.chuusai"            %% "shapeless"            % "2.3.3",
      "io.circe"               %% "circe-generic-extras" % "0.12.2",
      "io.circe"               %% "circe-parser"         % "0.12.2",
      "io.dylemma"             %% "xml-spac"             % "0.7",
      "net.arnx"               % "nashorn-promise"       % "0.1.2",
      "org.apache.kafka"       % "kafka-streams"         % "2.3.1",
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
    Test / parallelExecution := false,
    addCommandAlias("fmt", ";scalafmt;test:scalafmt;scalafmtSbt"),
    addCommandAlias("fmtCheck", ";scalafmtCheck;test:scalafmtCheck;scalafmtSbtCheck"),
    addCommandAlias("fullBuild", ";fmtCheck;clean;test;scripted")
  )
