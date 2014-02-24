import sbt._
import Keys._
import play.Project._

object Build {

  lazy val settings = playScalaSettings ++ Seq(
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

}

