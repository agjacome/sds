import com.typesafe.sbt.SbtGit._

name := "Smart Drug Search"

organization := "es.uvigo.esei"

versionWithGit

git.baseVersion := "0.1"

scalaVersion := "2.10.3"

// enable deprecation, feature and unchecked warnings info on compile
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// NER tools dependencies and resolvers (ABNER and Linnaeus from reenaudr's
// maven repo, and Oscar4 from Cambridge's Department of Chemistry)
resolvers ++= Seq(
  "Renaudr's Maven Repository" at "https://github.com/renaud/maven_repo/raw/master/snapshots",
  "Cambridge's Dpt. Chemistry" at "http://maven.ch.cam.ac.uk/m2repo"
)

libraryDependencies ++= Seq(
  "abner"                     % "abner"      % "1.5" ,
  "hu.u_szeged.rgai.bio.uima" % "linnaeus"   % "2.0" ,
  "uk.ac.cam.ch.wwmm.oscar"   % "oscar4-api" % "4.1.2"
    exclude("org.slf4j", "slf4j-simple")
    exclude("com.google.guava", "guava-collections")
)

// scala/java dependencies
libraryDependencies ++= Seq(
  "org.webjars"        %% "webjars-play"         % "2.2.1-2" ,
  "com.typesafe.slick" %% "slick"                % "2.0.0"   ,
  "com.typesafe.play"  %% "play-slick"           % "0.6.0.1" ,
  "com.typesafe.akka"  %% "akka-actor"           % "2.3.0"   ,
  "mysql"              %  "mysql-connector-java" % "5.1.29"  ,
  "com.twitter"        %% "util-collection"      % "6.12.1"
)

// testing dependencies
libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"                   % "2.1.0"   % "test" ,
  "org.scalamock"     %% "scalamock-scalatest-support" % "3.1.RC1" % "test" ,
  "com.typesafe.akka" %% "akka-testkit"                % "2.3.0"   % "test" ,
  "com.h2database"    %  "h2"                          % "1.3.175" % "test"
)

// Play framework extra dependencies
libraryDependencies ++= Seq(jdbc)

// import extra settings (from project/Build.scala)
Build.settings

