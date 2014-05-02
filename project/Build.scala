import sbt._
import sbtscalaxb.Plugin._

import Keys._
import ScalaxbKeys._

import play.Project._

object Build {

  private lazy val playSettings = playScalaSettings ++ Seq(
    confDirectory := baseDirectory.value / "conf",

    sourceDirectory   in Compile := baseDirectory.value / "src/main",
    scalaSource       in Compile := baseDirectory.value / "src/main/scala",
    javaSource        in Compile := baseDirectory.value / "src/main/java",
    resourceDirectory in Compile := baseDirectory.value / "src/main/resources",

    sourceDirectory   in Test := baseDirectory.value / "src/test",
    scalaSource       in Test := baseDirectory.value / "src/test/scala",
    javaSource        in Test := baseDirectory.value / "src/test/java",
    resourceDirectory in Test := baseDirectory.value / "src/test/resources",

    playAssetsDirectories := Seq(baseDirectory.value / "src/main/webapp")
  )

  private lazy val sbtSettings = scalaxbSettings ++ Seq(
    sourceGenerators in Compile <+= scalaxb in Compile,

    packageName  in scalaxb in Compile := "scalaxb.generated",
    wsdlSource   in scalaxb in Compile := baseDirectory.value / "src/main/resources/wsdl",
    xsdSource    in scalaxb in Compile := baseDirectory.value / "src/main/resources/xsd",
    wrapContents in scalaxb in Compile += "{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlineCitationType"
  )

  lazy val settings = playSettings ++ sbtSettings

}

