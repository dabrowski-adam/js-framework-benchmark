import scala.sys.process.Process

ThisBuild / version      := "1.0.0"
ThisBuild / scalaVersion := "3.3.1"

lazy val frontend = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name                            := "outwatch-benchmark-app",
    libraryDependencies            ++= List(
      "org.scala-js"       %%% "scala-js-macrotask-executor" % "1.1.1",
      "io.github.outwatch" %%% "outwatch"                    % "1.0.0",
    ),
    scalaJSLinkerConfig             ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSUseMainModuleInitializer := true,
  )

val buildFrontend = taskKey[Unit]("Build frontend")

buildFrontend := {
  // Generate Scala.js JS output for production
  (frontend / Compile / fullLinkJS).value

  // Install JS dependencies from package-lock.json
  val npmCiExitCode = Process("npm ci", cwd = (frontend / baseDirectory).value).!
  if (npmCiExitCode > 0) {
    throw new IllegalStateException(s"npm ci failed. See above for reason")
  }

  // Build the frontend with vite
  val buildExitCode = Process("npm run build", cwd = (frontend / baseDirectory).value).!
  if (buildExitCode > 0) {
    throw new IllegalStateException(s"Building frontend failed. See above for reason")
  }
}