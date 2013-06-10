name := "pfbase"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.twitter4j" % "twitter4j-core" % "3.0.3"
)

play.Project.playScalaSettings

lazy val aaaRoot = (project in file(".")).dependsOn(
    mpff
)

lazy val mpff = project in file("modules/mpff")

