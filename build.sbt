import com.typesafe.sbt.SbtGit._

name := "Smart Drug Search"

organization := "es.uvigo.esei"

versionWithGit

git.baseVersion := "0.1"

scalaVersion := "2.10.4"

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
  "com.typesafe.akka"       %% "akka-actor"           % "2.2.4"   ,
  "com.typesafe.slick"      %% "slick"                % "2.0.2"   ,
  "org.webjars"             %% "webjars-play"         % "2.2.1-2" ,
  "org.webjars"             %  "webjars-locator"      % "0.14"    ,
  "com.typesafe.play"       %% "play-slick"           % "0.6.0.1" ,
  "com.github.t3hnar"       %% "scala-bcrypt"         % "2.4"     ,
  "net.databinder.dispatch" %% "dispatch-core"        % "0.11.0"  ,
  "mysql"                   %  "mysql-connector-java" % "5.1.30"  ,
  "org.postgresql"          %  "postgresql"           % "9.3-1101-jdbc41"
)

// javascript/css/html dependencies
libraryDependencies ++= Seq(
  "org.webjars" % "underscorejs"         % "1.6.0-3" ,
  "org.webjars" % "jquery"               % "1.11.1"  ,
  "org.webjars" % "angularjs"            % "1.2.16-2" exclude("org.webjars", "jquery") ,
  "org.webjars" % "bootstrap"            % "3.1.1-1"  exclude("org.webjars", "jquery") ,
  "org.webjars" % "angular-ui-bootstrap" % "0.11.0-2" ,
  "org.webjars" % "font-awesome"         % "4.1.0"
)

// testing dependencies
libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"    % "2.1.5"  % "test" ,
  "org.scalacheck"    %% "scalacheck"   % "1.11.4" % "test" ,
  "com.typesafe.akka" %% "akka-testkit" % "2.2.4"  % "test" ,
  "org.mockito"       %  "mockito-core" % "1.9.5"  % "test"
)

// Play framework extra dependencies
libraryDependencies ++= Seq(jdbc, cache)

// import extra settings (from project/Build.scala)
Build.settings

// macros sub-project and main project with dependency on it
lazy val macros = (project in file("macros")).settings(
  libraryDependencies ++= Seq(
    "org.scala-lang" %  "scala-reflect" % scalaVersion.value,
    "org.scalatest"  %% "scalatest"     % "2.1.5" % "test" 
  )
)

lazy val main = project in file(".") dependsOn macros aggregate macros
