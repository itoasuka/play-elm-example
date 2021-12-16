name := """play-elm-example"""
organization := "jp.osd"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.7"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "jp.osd.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "jp.osd.binders._"

// フィンガープリントと gzip 圧縮を施す
pipelineStages := Seq(digest, gzip)

// Play Framework の開発モード実行時に npm の dev タスクおよび analyse タスクを実行するようにする
// ※鬱陶しいときは削除可
PlayKeys.playRunHooks += Npm.elmDev(baseDirectory.value)

// npm による production ビルドタスクキーの定義
lazy val npmBuild = taskKey[Unit]("Build production by npm when packaging the application")

// npm による production ビルドタスクの挙動設定
npmBuild := {
  if (Npm.runNpmBuild(baseDirectory.value) != 0) {
    throw new Exception("Something goes wrong when running npm build.")
  }
}

// sbt dist や sbt stage が実行される前に npm run build を稼働させるようにする
dist := (dist dependsOn npmBuild).value
stage := (stage dependsOn npmBuild).value

// npm による clean タスクキーの定義
lazy val npmClean = taskKey[Unit]("Clean by npm when cleaning the application")

// npm による clean タスクの挙動設定
npmClean := {
  if (Npm.runNpmClean(baseDirectory.value) != 0) {
    throw new Exception("Something goes wrong when running npm clean.")
  }
}

// sbt clean 時に npm run clean を稼働させるようにする
clean := (clean dependsOn npmClean).value

/* Windows で「入力行が長すぎます」などと言ってへこたれるのの対策 */

scriptClasspath := {
  val originalClasspath = scriptClasspath.value
  val manifest = new java.util.jar.Manifest()
  manifest.getMainAttributes.putValue("Class-Path", originalClasspath.mkString(" "))
  val classpathJar = (Universal / target).value / "lib" / "classpath.jar"
  IO.jar(Seq.empty, classpathJar, manifest)
  Seq(classpathJar.getName)
}
Universal / mappings += (((Universal / target).value / "lib" / "classpath.jar") -> "lib/classpath.jar")
