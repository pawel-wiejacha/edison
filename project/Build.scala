import sbt._
import Keys._
import sbtassembly.AssemblyKeys._
import sbt.inc.Analysis
import com.typesafe.sbt.SbtScalariform.scalariformSettings

object BuildSettings {
  val edisonVersion = "0.0.1"
  val buildScalaVersion = "2.11.6"

  val buildSettings =
    Defaults.coreDefaultSettings ++ Seq(
      organization := "edison",
      version := edisonVersion,
      scalaVersion := buildScalaVersion,
      scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
      shellPrompt := ShellPrompt.buildShellPrompt
    ) ++ scalariformSettings
}

object ShellPrompt {
  object Devnull extends ProcessLogger {
    def info(s: => String) {}
    def error(s: => String) {}
    def buffer[T](f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! Devnull).headOption
    getOrElse "-" stripPrefix "## "
  )

  val buildShellPrompt = {
    (state: State) =>
      {
        val currProject = Project.extract(state).currentProject.id
        "%s:%s:%s> ".format(
          currProject, currBranch, BuildSettings.edisonVersion
        )
      }
  }
}

object Dependencies {
  val json4s = "org.json4s" %% "json4s-jackson" % "3.2.11"
  val scopt = "com.github.scopt" %% "scopt" % "3.3.0"
  val snakeyaml = "org.yaml" % "snakeyaml" % "1.15"

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  val logback = "ch.qos.logback" % "logback-classic" % "1.0.9"

  val scaldi = "org.scaldi" %% "scaldi" % "0.5.6"

  val scalatest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"

  val testDeps = List(scalatest, scalacheck, scalamock)
}

object ScalaMockBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val edison = Project(
    "edison",
    file("."),
    settings = buildSettings ++ Seq(
      compile in Compile := Analysis.Empty,
      sources in Compile <<= Seq(core, service).map(sources in Compile in _).join.map(_.flatten)
    )
  ) aggregate (core, service)

  lazy val core = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
      name := "Edison Core",
      libraryDependencies ++= Seq(json4s) ++ testDeps
    )
  )

  lazy val service = Project(
    "service",
    file("service"),
    settings = buildSettings ++ Seq(
      name := "Edison Service",
      libraryDependencies ++= Seq(scopt, snakeyaml, logback, scalaLogging, scaldi) ++ testDeps,
      assemblyJarName in assembly := s"edison-service-onejar-${version.value}.jar"
    )
  ) dependsOn (core % "compile->compile;test->test")
}
