import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

name := "Wireless"
ThisBuild / organization := "com.goyeau"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-Xlint:unsound-match",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates"
)
ThisBuild / libraryDependencies += compilerPlugin(scalafixSemanticdb)
addCommandAlias("style", "; compile:scalafix; test:scalafix; compile:scalafmt; test:scalafmt; scalafmtSbt")
addCommandAlias(
  "styleCheck",
  "; compile:scalafix --check; test:scalafix --check; compile:scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck"
)

libraryDependencies ++= tests.value ++ circe.value ++ Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)

lazy val circe = Def.setting {
  val version = "0.11.1"
  Seq(
    "io.circe" %%% "circe-core"    % version,
    "io.circe" %%% "circe-generic" % version,
    "io.circe" %%% "circe-parser"  % version
  )
}

lazy val tests = Def.setting {
  val scalacheckVersion = "1.14.0"
  val scalatestVersion  = "3.0.7"
  Seq(
    "org.scalatest"  %%% "scalatest"  % scalatestVersion  % Test,
    "org.scalacheck" %%% "scalacheck" % scalacheckVersion % Test
  )
}
