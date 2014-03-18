import com.typesafe.sbt.SbtGit._

name := "Smart Drug Search"

organization := "es.uvigo.esei"

versionWithGit

git.baseVersion := "0.1"

scalaVersion := "2.10.3"

// enable deprecation, feature and unchecked warnings info on compile
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// use "/lib" as manually managed library directory (NER tools *.jar files)
unmanagedBase := baseDirectory.value / "lib"

// testing dependencies
libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"                   % "2.1.0"   % "test" ,
  "org.scalamock"     %% "scalamock-scalatest-support" % "3.1.RC1" % "test" ,
  "com.typesafe.akka" %% "akka-testkit"                % "2.2.3"   % "test" ,
  "com.h2database"    %  "h2"                          % "1.3.175" % "test"
)

// scala/java dependencies
libraryDependencies ++= Seq(
  "org.webjars"        %% "webjars-play"         % "2.2.1-2" ,
  "com.typesafe.slick" %% "slick"                % "2.0.0"   ,
  "com.typesafe.play"  %% "play-slick"           % "0.6.0.1" ,
  "com.typesafe.akka"  %% "akka-actor"           % "2.2.3"   ,
  "mysql"              %  "mysql-connector-java" % "5.1.29"  ,
  "com.twitter"        %% "util-collection"      % "6.12.1"
)

// play framework dependencies
libraryDependencies ++= Seq(jdbc)

// import extra settings (view in project/Build.scala)
Build.settings
