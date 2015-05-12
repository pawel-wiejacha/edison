scalaVersion := "2.11.5"

libraryDependencies ++= List(
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

scalariformSettings
