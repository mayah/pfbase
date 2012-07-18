import sbt._
import Keys._
import PlayProject._
import de.johoop.jacoco4sbt.JacocoPlugin._

object ApplicationBuild extends Build {
  val appName         = "pfbase"
  val appVersion      = "0.1-SNAPSHOT"

  lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)

  val appDependencies = Seq(
    "org.twitter4j" % "twitter4j-core" % "2.2.5",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA, settings = s).settings(
    externalIvySettings(),
    parallelExecution in jacoco.Config := false
  )
}
