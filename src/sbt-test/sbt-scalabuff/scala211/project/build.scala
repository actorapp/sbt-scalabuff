import sbt._
import Keys.libraryDependencies

import scalabuff.ScalaBuffPlugin._

object build extends Build {
  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = Project.defaultSettings ++ scalabuffSettings ++ Seq(
      "actor at bintray" at "http://dl.bintray.com/actor/maven"
    )).configs(ScalaBuff)
}
