name         := "sds"
organization := "es.uvigo.ei.sing"
version      := "1.0"

scalaVersion  := "2.11.5"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  "-Xfuture",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import"
  // "-Yno-adapted-args", // not respected by scalaxb, so it cannot be used
  // "-Xfatal-warnings",  // scalaxb generated code emits warnings
)

resolvers ++= Seq(
  "Cambridge's Dpt. Chemistry"      at "http://maven.ch.cam.ac.uk/m2repo"                          ,
  "Renaudr's Maven Repository"      at "https://github.com/renaud/maven_repo/raw/master/snapshots" ,
  "Bioinformatics UA.pt Repository" at "http://bioinformatics.ua.pt/maven/content/groups/public"   ,
  "Scalaz Bintray Repo"             at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  // scala lang modules
  "org.scala-lang.modules"  %% "scala-xml"                % "1.0.2" ,
  "org.scala-lang.modules"  %% "scala-parser-combinators" % "1.0.1" ,

  // NER Tools
  "abner"                     % "abner"      % "1.5" ,
  "hu.u_szeged.rgai.bio.uima" % "linnaeus"   % "2.0" ,
  "uk.ac.cam.ch.wwmm.oscar"   % "oscar4-api" % "4.2.2" exclude("org.slf4j", "slf4j-simple") exclude("com.google.guava", "guava") ,
  "pt.ua.tm"                  % "gimli"      % "1.0.2" exclude("opennlp", "tools") ,

  // core java/scala
  "com.typesafe.akka"       %% "akka-actor"      % "2.3.9"    ,
  "com.typesafe.slick"      %% "slick"           % "2.1.0"    ,
  "org.webjars"             %% "webjars-play"    % "2.4.0-M2" ,
  "com.typesafe.play"       %% "play-slick"      % "0.9.0-M3" ,
  "com.typesafe.play"       %% "play-jdbc"       % "2.4.0-M2" ,
  "com.typesafe.play"       %% "play-cache"      % "2.4.0-M2" ,
  "com.github.t3hnar"       %% "scala-bcrypt"    % "2.4"      ,
  "org.jsoup"               %  "jsoup"           % "1.8.1"    ,
  "net.databinder.dispatch" %% "dispatch-core"   % "0.11.2"   ,

  // database connectors
  "mysql"            %  "mysql-connector-java" % "5.1.34" ,
  "org.mariadb.jdbc" %  "mariadb-java-client"  % "1.1.7"  ,

  // css/javascript
  "org.webjars" % "angularjs"            % "1.2.16-2" exclude("org.webjars", "jquery") ,
  "org.webjars" % "bootstrap"            % "3.1.1-1"  exclude("org.webjars", "jquery") ,
  "org.webjars" % "jquery"               % "1.9.1"   ,
  "org.webjars" % "underscorejs"         % "1.7.0"   ,
  "org.webjars" % "angular-ui-bootstrap" % "0.12.0"  ,
  "org.webjars" % "font-awesome"         % "4.3.0-1" ,

  // testing
  "org.scalatest"     %% "scalatest"    % "2.2.4"   % "test" ,
  "org.scalacheck"    %% "scalacheck"   % "1.12.2"  % "test" ,
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9"   % "test" ,
  "org.mockito"       %  "mockito-core" % "1.10.19" % "test"
)

import PlayKeys._
import ScalaxbKeys._

lazy val sds = (project in file(".")).enablePlugins(PlayScala, SbtWeb).settings(scalaxbSettings : _*).settings(

  confDirectory := baseDirectory.value / "src/main/resources/conf" ,

  sourceDirectory   in Compile := baseDirectory.value / "src/main"           ,
  scalaSource       in Compile := baseDirectory.value / "src/main/scala"     ,
  javaSource        in Compile := baseDirectory.value / "src/main/java"      ,
  resourceDirectory in Compile := baseDirectory.value / "src/main/resources" ,
  sourceDirectory   in Test    := baseDirectory.value / "src/test"           ,
  scalaSource       in Test    := baseDirectory.value / "src/test/scala"     ,
  javaSource        in Test    := baseDirectory.value / "src/test/java"      ,
  resourceDirectory in Test    := baseDirectory.value / "src/test/resources" ,

  unmanagedResourceDirectories in Assets := Seq(baseDirectory.value / "src/main/webapp") ,
  pipelineStages := Seq(rjs, digest, gzip) ,

  dispatchVersion  in (Compile, scalaxb) := "0.11.2" ,
  packageName      in (Compile, scalaxb) := "scalaxb.generated"                             ,
  wsdlSource       in (Compile, scalaxb) := baseDirectory.value / "src/main/resources/wsdl" ,
  xsdSource        in (Compile, scalaxb) := baseDirectory.value / "src/main/resources/xsd"  ,
  sourceGenerators in Compile <+= scalaxb in Compile ,

  shellPrompt := { state => name.value + " » " }

)
