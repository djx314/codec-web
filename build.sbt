scalaVersion := scalaV.v3
name         := "codec-web"

scalafmtOnCompile := true

libraryDependencies += "net.scalax.simple" %% "simple-codec-slick" % "0.0.2-M16"
libraryDependencies += "net.scalax.simple" %% "simple-codec-circe" % "0.0.2-M16"

scalacOptions ++= Seq("-Ykind-projector", "-experimental")

Global / onChangedBuildSource := ReloadOnSourceChanges
