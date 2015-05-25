name         := "sds"
organization := "es.uvigo.ei.sing"
version      := "1.1.0"

scalaVersion  := "2.11.6"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  // "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  // "-Yno-adapted-args",
  // "-Yno-imports",
  // "-Yno-predef",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
  // "-Ywarn-unused-import"
)

resolvers ++= Seq(
  "Cambridge's Dpt. Chemistry"      at "http://maven.ch.cam.ac.uk/m2repo"                          ,
  "Renaudr's Maven Repository"      at "https://github.com/renaud/maven_repo/raw/master/snapshots" ,
  "Bioinformatics UA.pt Repository" at "http://bioinformatics.ua.pt/maven/content/groups/public"   ,
  "Scalaz Bintray Repo"             at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  // scala lang modules
  "org.scala-lang.modules"  %% "scala-xml"                % "1.0.4" ,
  "org.scala-lang.modules"  %% "scala-parser-combinators" % "1.0.4" ,

  // NER Tools
  "abner"                     % "abner"      % "1.5" ,
  "hu.u_szeged.rgai.bio.uima" % "linnaeus"   % "2.0" ,
  "uk.ac.cam.ch.wwmm.oscar"   % "oscar4-api" % "4.2.2" exclude("org.slf4j", "slf4j-simple")  exclude("com.google.guava", "guava") ,
  "pt.ua.tm"                  % "gimli"      % "1.0.2" exclude("org.slf4j", "slf4j-log4j12") exclude("opennlp", "tools") ,

  // core java/scala
  "com.typesafe.akka"       %% "akka-actor"      % "2.3.11"    ,
  "com.typesafe.play"       %% "play-jdbc"       % "2.4.0-RC5" ,
  "com.typesafe.play"       %% "play-cache"      % "2.4.0-RC5" ,
  "com.typesafe.slick"      %% "slick"           % "3.0.0"     ,
  "com.typesafe.play"       %% "play-slick"      % "1.0.0-RC3" ,
  "com.github.t3hnar"       %% "scala-bcrypt"    % "2.4"       ,
  "org.jsoup"               %  "jsoup"           % "1.8.2"     ,
  "net.databinder.dispatch" %% "dispatch-core"   % "0.11.2"    ,

  // database connectors
  "mysql"            % "mysql-connector-java" % "5.1.35" ,
  "org.mariadb.jdbc" % "mariadb-java-client"  % "1.1.8"  ,

  // css/javascript
  "org.webjars" % "angularjs"            % "1.3.15"  exclude("org.webjars", "jquery") ,
  "org.webjars" % "bootstrap"            % "3.1.1-1" exclude("org.webjars", "jquery") ,
  "org.webjars" % "jquery"               % "1.11.3"  ,
  "org.webjars" % "underscorejs"         % "1.8.3"   ,
  "org.webjars" % "requirejs"            % "2.1.17"  ,
  "org.webjars" % "angular-ui-bootstrap" % "0.13.0"  ,
  "org.webjars" % "font-awesome"         % "4.3.0-2" ,
  "org.webjars" % "sigma.js"             % "1.0.3"   ,

  // testing
  "org.scalatest"     %% "scalatest"    % "2.2.5"   % "test" ,
  "org.scalacheck"    %% "scalacheck"   % "1.12.3"  % "test" ,
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11"  % "test" ,
  "org.mockito"       %  "mockito-core" % "1.10.19" % "test"
)

import PlayKeys._

val sds = (project in file(".")).enablePlugins(PlayScala).settings(scalaxbSettings : _*).settings(

  sourceDirectory in Compile := baseDirectory.value / "src/main" ,
  sourceDirectory in Test    := baseDirectory.value / "src/test" ,

  scalaSource in Compile := (sourceDirectory in Compile).value / "scala" ,
  scalaSource in Test    := (sourceDirectory in    Test).value / "scala" ,

  javaSource in Compile := (sourceDirectory in Compile).value / "java" ,
  javaSource in Test    := (sourceDirectory in    Test).value / "java" ,

  resourceDirectory in Compile := (sourceDirectory in Compile).value / "resources" ,
  resourceDirectory in Test    := (sourceDirectory in    Test).value / "resources" ,

  sourceDirectory in Assets := (sourceDirectory in Compile).value / "assets" ,
  pipelineStages := Seq(rjs, digest, gzip) ,

  sourceDirectory in (Compile, TwirlKeys.compileTemplates) := (sourceDirectory in Compile).value / "twirl" ,
  TwirlKeys.templateImports += "es.uvigo.ei.sing.sds.controller._" ,

  ScalaxbKeys.dispatchVersion  in (Compile, ScalaxbKeys.scalaxb) := "0.11.2" ,
  ScalaxbKeys.packageName      in (Compile, ScalaxbKeys.scalaxb) := "scalaxb.generated"                           ,
  ScalaxbKeys.wsdlSource       in (Compile, ScalaxbKeys.scalaxb) := (resourceDirectory in Compile).value / "wsdl" ,
  ScalaxbKeys.xsdSource        in (Compile, ScalaxbKeys.scalaxb) := (resourceDirectory in Compile).value / "xsd"  ,

  ScalaxbKeys.async in (Compile, ScalaxbKeys.scalaxb) := false ,
  sourceGenerators in Compile <+= ScalaxbKeys.scalaxb in Compile ,

  shellPrompt := { state => name.value + " » " }

)
