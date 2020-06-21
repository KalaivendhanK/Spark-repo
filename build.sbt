import sbt._
import scalariform.formatter.preferences._


lazy val commonDeps = Seq("org.apache.spark" %% "spark-core" % "2.4.3",
  "org.apache.spark" %% "spark-sql" % "2.4.3",
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test")

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ypartial-unification"
)

lazy val commonSettings = Seq(
  organization := "com.home.projects",
  scalaVersion := "2.12.7",
  libraryDependencies ++= commonDeps,
  scalariformPreferences := scalariformPreferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(AlignArguments, true)
    .setPreference(CompactControlReadability, true)
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DoubleIndentMethodDeclaration, true)
    .setPreference(FirstParameterOnNewline, Force)
    .setPreference(IndentLocalDefs, true)
    .setPreference(IndentPackageBlocks, true)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(RewriteArrowSymbols, false) //Set thie to true to see the elemental theme like symbol (=>)
)

 // Root Project
lazy val root = (project in file(".")).
  aggregate(scalaProgramming, mysqlSpark)
  .settings(
    aggregate in update := false
  )

 /*
 Project 1. ScalaProgramming
 This project contains the various implementation of Sets in scala library
 Based on the guidance from Channel DevInsideYou
 */
lazy val scalaProgramming = (project in file("scalaProgramming")).
  settings(
    commonSettings: _*
  ).
  settings(
    name := "scalaProgramming"
  ).
  settings(
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )

/*
Project 2. mySqlSpark
This project has the examples of spark
 */
lazy val mysqlSpark = (project in file("mysqlSpark")).
  disablePlugins(sbtassembly.AssemblyPlugin).
  settings(
    commonSettings: _*
  ).
  settings(
    name := "mysqlSpark"
  ).
  settings(
    libraryDependencies ++= Seq("mysql" % "mysql-connector-java" % "5.1.16")
  )

/*
Project 3. CustomSetDev
This project has my personal learings on various Funcitonal programming concepts like
monads, monoids, Functors , Programming style using Tagless Final, etc.
 */
lazy val FPConcepts = (project in file("FPConcepts")).
  disablePlugins(sbtassembly.AssemblyPlugin).
  settings(
    Seq(
      organization := "com.home.projects",
      scalaVersion := "2.12.7",
      name := "customSetDev",
      libraryDependencies ++= Seq(
        "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP10" % "test",
        "org.scalacheck" % "scalacheck_2.12" % "1.14.0" % "test",
        "org.typelevel" %% "cats-core" % "2.1.1",
        "org.typelevel" %% "simulacrum" % "1.0.0"
      ),
      scalacOptions := commonScalacOptions,
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
    )
  )
