package eisner

import java.io.File
import java.security.MessageDigest

import scala.reflect.internal.util.{AbstractFileClassLoader, BatchSourceFile, ScalaClassLoader}
import scala.reflect.io.VirtualDirectory
import scala.tools.nsc.{Global, Settings}

private[eisner] trait SnippetSupport {
  def getTopologies(snippet: String, bootClasspath: Seq[File], classpath: Seq[File]): Seq[(String, String)] =
    new Compiler(bootClasspath, classpath).eval(snippet)
}

private[eisner] final class Compiler(bootClasspath: Seq[File], classpath: Seq[File]) {
  private[this] final val target = new VirtualDirectory("(memory)", None)

  private[this] final val settings = new Settings()
  settings.bootclasspath.value = bootClasspath.map(_.getAbsolutePath).mkString(":")
  settings.classpath.value = classpath.map(_.getAbsolutePath).mkString(":")
  settings.outputDirs.setSingleOutput(target)

  private[this] final val global = new Global(settings)
  private[this] final val run    = new global.Run

  private[this] final val cl = new AbstractFileClassLoader(
    target,
    ScalaClassLoader.fromURLs((bootClasspath ++ classpath).map(_.toURI.toURL), Thread.currentThread.getContextClassLoader)
  )

  private[this] final def className(code: String): String =
    s"sha${BigInt(1, MessageDigest.getInstance("SHA-1").digest(code.getBytes)).toString(16)}"

  private[this] final def mkClass(className: String, code: String) =
    s"""final class $className extends (() => Seq[(String, String)]) {
      |  override final def apply(): Seq[(String, String)] = {
      |    $code
      |  }.map { case (n, t) => n -> t.describe.toString }
      |}""".stripMargin

  private[this] final def compile(code: String): Class[_] = {
    val name = className(code)
    run.compileSources(new BatchSourceFile("(inline)", mkClass(name, code)) :: Nil)
    cl.loadClass(name)
  }

  def eval(code: String): Seq[(String, String)] =
    compile(code).getConstructor().newInstance().asInstanceOf[() => Seq[(String, String)]].apply()
}
