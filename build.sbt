cancelable := true

crossScalaVersions := Seq("2.11.12", "2.12.10", "2.13.0")

developers := List(
  Developer(
    "mslinn",
    "Mike Slinn",
    "mslinn@micronauticsresearch.com",
    url("https://github.com/mslinn")
  )
)

licenses +=  ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

// define the statements initially evaluated when entering 'console', 'console-quick', but not 'console-project'
initialCommands in console := """
                                |""".stripMargin

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

libraryDependencies ++= Seq(
  "com.micronautics"  %% "has-value"     % "1.1.0" withSources(),
  "org.scala-lang"    %  "scala-reflect" % scalaVersion.value,
  //
  "org.scalatest"     %% "scalatest"  % "3.0.8" % Test withSources(),
  "junit"             %  "junit"      % "4.12"  % Test
)

logLevel := Level.Warn

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn

// Level.INFO is needed to see detailed output when running tests
logLevel in test := Level.Info

name := "has-id"

organization := "com.micronautics"

resolvers += "micronautics/scala on bintray" at "https://dl.bintray.com/micronautics/scala"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-target:jvm-1.8",
  "-unchecked",
  "-Xlint"
)

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  bd: File => Seq[String](
     "-sourcepath", bd.getAbsolutePath,
     "-doc-source-url", "https://github.com/mslinn/has-id/tree/master€{FILE_PATH}.scala"
  )
}.value

scalaVersion := "2.13.0"

scmInfo := Some(
  ScmInfo(
    url("https://github.com/mslinn/has-id"),
    "git@github.com:mslinn/has-id.git"
  )
)

sublimeTransitive := true

ThisBuild / turbo := true

version := "1.3.0"
