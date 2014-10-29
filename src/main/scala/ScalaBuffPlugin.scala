package scalabuff

import sbt._
import Keys._
import sbt.Fork

import java.io.File

object ScalaBuffPlugin extends Plugin {
  val ScalaBuff = config("scalabuff").hide

  val scalabuff = TaskKey[Seq[File]]("scalabuff", "Generate Scala sources from protocol buffers definitions")
  val scalabuffArgs = SettingKey[Seq[String]]("scalabuff-args", "Extra command line parameters to scalabuff.")
  val scalabuffMain = SettingKey[String]("scalabuff-main", "ScalaBuff main class.")
  val scalabuffVersion =  SettingKey[String]("scalabuff-version", "ScalaBuff version.")

  lazy val scalabuffSettings = Seq[Project.Setting[_]](
    scalabuffArgs := Seq(),
    scalabuffMain := "net.sandrogrzicic.scalabuff.compiler.ScalaBuff",
    scalabuffVersion := "1.3.9.2",
    libraryDependencies <++= (scalabuffVersion in ScalaBuff)(version => 
      Seq(
        "net.sandrogrzicic" %% "scalabuff-compiler" % version % ScalaBuff.name,
        "net.sandrogrzicic" %% "scalabuff-runtime" % version excludeAll(
		  ExclusionRule(organization = "com.google.protobuf")
		),
		"com.google.protobuf" % "protobuf-java" % "2.5.0"
      )
    ),
    sourceDirectory in ScalaBuff <<= (sourceDirectory in Compile),

    managedClasspath in ScalaBuff <<= (classpathTypes, update) map { (ct, report) =>
      Classpaths.managedJars(ScalaBuff, ct, report)
    },

    scalabuff <<= (
      sourceDirectory in ScalaBuff,
      sourceManaged in ScalaBuff,
      scalabuffMain in ScalaBuff,
      scalabuffArgs in ScalaBuff,
      managedClasspath in ScalaBuff,
      javaHome,
      streams,
      cacheDirectory
    ).map(process),

    sourceGenerators in Compile <+= (scalabuff).task
  )

  private def process(
    source: File,
    sourceManaged: File,
    mainClass: String,
    args: Seq[String],
    classpath: Classpath,
    javaHome: Option[File],
    streams: TaskStreams,
    cache: File
  ): Seq[File] = {
    val input = source / "protobuf"
    if (input.exists) {
      val output = sourceManaged
      val commandLineOptions = List(
          "-cp", classpath.map(_.data).mkString(File.pathSeparator), mainClass,
          "--proto_path=" + input.toString, // processing single .proto files conflicts with protobuf import statements -> rather re-process all .protos if any has changed
          "--scala_out=" + output.toString
      ) ++ args
      val cached = FileFunction.cached(cache / "scalabuff", FilesInfo.lastModified, FilesInfo.exists) {
        (in: Set[File]) => {
          IO.delete(output)
          IO.createDirectory(output)
          Fork.java(
            javaHome,
            commandLineOptions,
            streams.log
          )
          (output ** ("*.scala")).get.toSet
        }
      }
      cached((input ** "*.proto").get.toSet).toSeq
    } else Nil
  }
}
