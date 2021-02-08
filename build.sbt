name := "MiniBlockchain"

version := "0.1"

scalaVersion := "2.13.3"
scalacOptions ++= Seq("-deprecation", "-feature")
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
libraryDependencies += "io.monix" %% "monix" % "3.3.0",