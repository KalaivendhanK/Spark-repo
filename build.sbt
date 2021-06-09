import sbt._
import scalariform.formatter.preferences._

//sbt shell command line aliases
addCommandAlias("cd", "project")
addCommandAlias("ll", "projects")
addCommandAlias("cc", "console")

lazy val commonDeps = Seq(
  "org.scalatest"  %% "scalatest"  % "3.2.0-SNAP10" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.0"       % "test"
)

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
    .setPreference(RewriteArrowSymbols, false)
)

// Root Project
lazy val root = (project in file("."))
//  .aggregate(scalaProgramming, mysqlSpark)
//  .settings(
//    aggregate in update := false
//  )

/*
 Project 1. ScalaProgramming
 This project contains the various implementation of Sets in scala library
 Based on the guidance from Channel DevInsideYou
 */
lazy val scalaProgramming = (project in file("scalaProgramming"))
  .settings(
    commonSettings: _*
  ).settings(
    initialCommands in console := "import com.home.collections._",
    name                       := "scalaProgramming",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    }
  )

/*
Project 2. mySqlSpark
This project has the examples of spark and aws glue libraries as well
 */
lazy val mysqlSpark = (project in file("mysqlSpark"))
  .disablePlugins(sbtassembly.AssemblyPlugin).settings(
    commonSettings: _*
  ).settings(
    name := "mysqlSpark"
  ).settings(
    //resolver to download the glue spark libs - The jar is located in official aws s3 bucket.hence resolver is required
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "aws-glue-etl-artifacts" at "https://aws-glue-etl-artifacts.s3.amazonaws.com/release/"
    ),
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"           % "2.4.3",
      "org.apache.spark" %% "spark-sql"            % "2.4.3",
      "org.apache.spark" %% "spark-hive"           % "2.4.3",
      "mysql"             % "mysql-connector-java" % "5.1.16",
      "org.apache.hive"   % "hive-jdbc"            % "3.1.1"

      //commented out the glue libs due to conflict between glue libs with spark libs.
      // There is a dedicated project AWSGlueProject to work with glue stuffs
      //"com.amazonaws" % "aws-java-sdk-glue" % "1.11.918"
      //"com.amazonaws" % "AWSGlueETL" % "1.0.0"
    ),
    dependencyOverrides ++= {
      Seq(
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1",
        "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.6.7",
        "com.fasterxml.jackson.core"    % "jackson-core"         % "2.6.7",
        //THere are multiple version of hadoop libraries , overriding to version 2.6.5 to avoid conflicts
        "org.apache.hadoop" % "hadoop-annotations"           % "2.6.5",
        "org.apache.hadoop" % "hadoop-auth"                  % "2.6.5",
        "org.apache.hadoop" % "hadoop-common"                % "2.6.5",
        "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.6.5"
      )
    },
    //Excluding the slf4j logger from spark due to multiple slf4j logger libraries conflicts
    libraryDependencies ~= { _.map(_.exclude("org.apache.logging.log4j", "log4j-slf4j-impl")) }
  )

/*
Project 3. FPConcepts
This project has my personal learings on various Funcitonal programming concepts like
monads, monoids, Functors , Programming style using Tagless Final, etc.
 */
val specs2Version = "4.9.4" // use the version used by discipline
val specs2Core = "org.specs2"       %% "specs2-core"       % specs2Version
val specs2Scalacheck = "org.specs2" %% "specs2-scalacheck" % specs2Version
val scalacheck = "org.scalacheck"   %% "scalacheck"        % "1.12.4"

lazy val FPConcepts = (project in file("FPConcepts"))
  .
//  disablePlugins(sbtassembly.AssemblyPlugin).
  settings(
    organization := "com.home.projects",
    scalaVersion := "2.12.11",
    name         := "FPConcepts",
    libraryDependencies ++= Seq(
      //Libraries specific to cats
      "org.scalatest"    % "scalatest_2.12" % "3.2.0-SNAP10",
      "org.typelevel"   %% "cats-core"      % "2.1.1",
      "org.typelevel"   %% "simulacrum"     % "1.0.0",
      "org.scalamacros" %% "resetallattrs"  % "1.0.0",
      //Libraries for parsing json and working with https requests
      "io.spray"    %% "spray-json"   % "1.3.5",
      "net.liftweb" %% "lift-json"    % "3.4.1",
      "org.scalaj"  %% "scalaj-http"  % "2.3.0",
      "com.lihaoyi" %% "upickle"      % "0.7.1",
      "io.circe"    %% "circe-parser" % "0.14.0-M1",
      specs2Core,
      specs2Scalacheck,
      scalacheck,
      // ZIO Specific libraries
      "dev.zio" %% "zio" % "1.0.5"
    ),
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
      .setPreference(RewriteArrowSymbols, true),
    scalacOptions := commonScalacOptions,
    // paradise plugin helps in expanding the macros during compile time
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    //Kind projector is used to shorten the kind representation in case of higher order kinds
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
  )

/*
Project 4. AWSProjects
This project has the programs related to the working on the services in AWS such as
Lambda, Glue, etc using Scala and Funcitonal Programming Style.
 */
lazy val AWSProjects = (project in file("AWSProjects"))
  .
//  disablePlugins(sbtassembly.AssemblyPlugin).
  settings(
    organization    := "com.home.projects",
    version         := "1.0",
    scalaVersion    := "2.12.11",
    retrieveManaged := true,
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core"   % "1.0.0",
      "com.amazonaws" % "aws-lambda-java-events" % "1.0.0",
      "com.amazonaws" % "aws-java-sdk-s3"        % "1.11.179"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    }
  )

/*
Project 5. Dotty Project
This project has the sample test programms to check the DOtty conpailer capabilities and soon to be scala 3
There is a separate dedicated Dotty project maintained with its own git repo
 */

/*
val dottyVersion = "3.0.0-M1"
val scala213Version = "2.13.1"

lazy val dotty = project
  .in(file("dottycross-scalaprogramming"))
  .settings(
    name := "dotty-cross",
    version := "0.1.0",

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",

    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,

    // To cross compile with Dotty and Scala 2
    crossScalaVersions := Seq(dottyVersion, scala213Version)
  )
 */

/*
Project 6: ZparkIO
This project users spark on top of ZIO library
 */

lazy val zparkIOSettings = Seq(
  organization := "com.home.zparkIO",
  scalaVersion := "2.12.12",
  version      := "0.0.1"
)

lazy val zparkio = (project in file("zparkio"))
  .settings(
    name := "zparkio",
    zparkIOSettings,
    libraryDependencies ++= Seq(
      // https://mvnrepository.com/artifact/org.apache.spark/spark-core
      "org.apache.spark" %% "spark-core" % "2.4.3" % Provided,
      // https://mvnrepository.com/artifact/org.apache.spark/spark-sql
      "org.apache.spark" %% "spark-sql" % "2.4.3" % Provided,
      // https://github.com/leobenkel/ZparkIO
      "com.leobenkel" %% "zparkio"                % "3.1.1_0.14.0",
      "com.leobenkel" %% "zparkio-config-scallop" % "3.1.1_0.14.0",
      "com.leobenkel" %% "zparkio-test"           % "3.1.1_0.14.0" % Test,
      // https://www.scalatest.org/
      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    )
  )
