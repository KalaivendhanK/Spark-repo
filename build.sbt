import scalariform.formatter.preferences._
import sbt._


lazy val commonDeps = Seq("org.apache.spark" %% "spark-core" % "2.4.3",
"org.apache.spark" %% "spark-sql" % "2.4.3",
"org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % "test",
"org.scalacheck" %% "scalacheck" % "1.14.0" % "test")

lazy val commonSettings = Seq(
  organization := "com.home.projects",
  scalaVersion := "2.12.7",
  libraryDependencies ++= commonDeps,
  triggeredMessage := Watched.clearWhenTriggered,
  scalariformPreferences := scalariformPreferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(AlignArguments, true)
    .setPreference(CompactControlReadability ,true)
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DoubleIndentMethodDeclaration, true)
    .setPreference(FirstParameterOnNewline, Force)
    .setPreference(IndentLocalDefs, true)
    .setPreference(IndentPackageBlocks, true)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk,true)
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(RewriteArrowSymbols, false) //Set thie to true to see the elemental theme like symbol (=>)
)

lazy val root = (project in file(".")).
                aggregate(scalaProgramming,mysqlSpark)
                .settings(
                    aggregate in update := false
                )

lazy val scalaProgramming = (project in file("scalaProgramming")).
                            settings(
                                commonSettings: _*
                            ).
                            settings(
                                name := "scalaProgramming"
                            ).
			    settings(
				 assemblyMergeStrategy in assembly :=  {
				 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
				 case x => MergeStrategy.first
				}
			    )

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


