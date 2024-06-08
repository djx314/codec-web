scalaVersion := scalaV.v3
name         := "codec-web"

scalafmtOnCompile := true

libraryDependencies += "net.scalax.simple" %% "simple-codec-slick" % "0.0.2-M16"
libraryDependencies += "net.scalax.simple" %% "simple-codec-circe" % "0.0.2-M16"
libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= libScalax.`http4s-Release-ember-server`.value
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.10.8"

scalacOptions ++= Seq("-Ykind-projector", "-experimental")

Global / onChangedBuildSource := ReloadOnSourceChanges
