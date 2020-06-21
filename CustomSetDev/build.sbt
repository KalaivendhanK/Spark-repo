version := "0.0.1-SNAPSHOT"

organization := "freekzhomework"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP10" % "test",
    "org.scalacheck" % "scalacheck_2.12" % "1.14.0" % "test",
    "org.typelevel" %% "cats-core" % "2.1.1",
    "org.typelevel" %% "simulacrum" % "1.0.0"
)


scalacOptions := Seq(
    "-deprecation",
    "-feature",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ypartial-unification"
)

triggeredMessage := Watched.clearWhenTriggered

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

// This section includes components related to scala.js
//enablePlugins(ScalaJSPlugin)
//scalaJSUseMainModuleInitializer := true