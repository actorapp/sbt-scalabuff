sbtPlugin := true

name := "sbt-scalabuff"

version := "0.3.1_1.3.9.2"

organization := "com.github.sbt"

scalaVersion        := "2.10.2"

crossScalaVersions  := Seq("2.10.0", "2.10.2")

scriptedSettings

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dproject.version=" + version.value)

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("actor")

bintray.Keys.repository in bintray.Keys.bintray := "sbt-plugins"