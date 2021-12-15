import play.sbt.PlayRunHook
import sbt._

import scala.language.postfixOps
import scala.sys.process.Process

object Npm {
  def elmDev(base: File): PlayRunHook = {
    object NpmProcess extends PlayRunHook {
      private[this] var devProcess: Option[Process] = None
      private[this] var analyseProcess: Option[Process] = None

      override def afterStarted(): Unit = {
        devProcess = Some(npmRun(base, "dev").run)
        analyseProcess = Some(npmRun(base, "analyse").run)
      }

      override def afterStopped(): Unit = {
        devProcess.foreach(_.destroy())
        devProcess = None
        analyseProcess.foreach(_.destroy())
        analyseProcess = None
      }
    }

    NpmProcess
  }

  def runNpmBuild(base: File) = {
    npmRun(base, "build") !
  }

  def runNpmClean(base: File) = {
    npmRun(base, "clean") !
  }

  private def npmRun(base: File, runScript: String) = {
    val command = if (System.getProperty("file.separator") == "\\") {
      // 実行環境が Windows のとき npm が npm.cmd などとして提供されていることがあるので
      // cmd 経由で実行した方がより確実
      Seq("cmd", "/c", "npm", "run", runScript)
    } else {
      Seq("npm", "run", runScript)
    }
    Process(command, base / "ui")
  }

}
