name := """pubmed"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)
