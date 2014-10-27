import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

name := "elastic4s"

organization := "com.sksamuel.elastic4s"

version := "1.3.2-z3"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

publishArtifact in Test := false

parallelExecution in Test := false

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")

instrumentSettings

jennerProjectId := "7b0a65b8-7c9a-4f25-864c-3f3e009752b9"

jennerApiKey := "04ba7a59-5bad-4862-9bc2-a7e1aa0b93ab"

libraryDependencies ++= Seq(
  "org.elasticsearch"              %  "elasticsearch"               % "1.3.2",
  "org.slf4j"                      %  "slf4j-api"                   % "1.7.7",
  "commons-io"                     %  "commons-io"                  % "2.4",
  "com.fasterxml.jackson.core"     %  "jackson-core"                % "2.4.2"  % "optional" ,
  "com.fasterxml.jackson.core"     %  "jackson-databind"            % "2.4.2"  % "optional" ,
  "com.fasterxml.jackson.module"   %% "jackson-module-scala"        % "2.4.2"  % "optional"  exclude ("org.scalatest", "scalatest_2.10.0"),
  "log4j"                          %  "log4j"                       % "1.2.17" % "test",
  "org.slf4j"                      %  "log4j-over-slf4j"            % "1.7.7"  % "test",
  "org.mockito"                    %  "mockito-all"                 % "1.9.5"  % "test",
  "org.scalatest"                  %% "scalatest"                   % "2.2.1"  % "test"
)

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)

publishTo <<= (version) { version: String =>
      val zestia = "https://zestia.artifactoryonline.com/zestia/"
            if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at zestia + "libs-snapshots-local/")
                        else                                   Some("releases"  at zestia + "libs-releases-local/")
}

// if publish fails (e.g. the artifact has already been uploaded) then throw success for jenkins anyway
publish <<= publish mapR {
  case Inc(inc: Incomplete) => 3
  case Value(v) => v
}

credentials += Credentials(Path.userHome / ".artifactory-credentials")
