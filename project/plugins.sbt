//Plugin to create a fat jar with all the dependencies.
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")

//Plugin to format the scala code. Format options are defined in project-root/scalariform.sbt file
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.3")

//Plugin to create test reports .
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

//Plugin to integrate with scala.js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.0.1")

//Plugin to support the cats library
addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")

//Plugin to run the gitter templates from SBT
//addSbtPlugin("org.foundweekends.giter8" %% "sbt-giter8" % "0.11.0")