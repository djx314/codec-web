scalaVersion := scalaV.v3
name         := "codec-web"

scalafmtOnCompile := true

val tapirVersion = "1.10.8"
libraryDependencies += "net.scalax.simple" %% "simple-codec-slick" % "0.0.2-M16"
libraryDependencies += "net.scalax.simple" %% "simple-codec-circe" % "0.0.2-M16"

libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= libScalax.`http4s-Release-ember-server`.value

libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % tapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % tapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion

scalacOptions ++= Seq("-Ykind-projector", "-experimental")

Global / onChangedBuildSource := ReloadOnSourceChanges
