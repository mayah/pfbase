import sbt._
import Keys._
import PlayProject._
import de.johoop.jacoco4sbt.JacocoPlugin._

object ApplicationBuild extends Build {
  val appName         = "pfbase"
  val appVersion      = "0.1-SNAPSHOT"

  lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)

  val appDependencies = Seq(
    jdbc,
    anorm,
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "junit" % "junit" % "4.11" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test",
    "org.twitter4j" % "twitter4j-core" % "3.0.3",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    externalIvySettings(),
    parallelExecution in jacoco.Config := false
  )
}
