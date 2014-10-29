import sbt._
import Keys._
import bintray.Plugin._

object ScalaBuffBuild extends Build {
  lazy val project = Project(
    id = "root", 
    base = file("."),
    settings = defaultSettings
  )

  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
  ) ++ bintraySettings

}
