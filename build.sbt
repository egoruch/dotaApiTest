
scalaVersion in ThisBuild := "2.11.7"

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-optimize"
)

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "com.google.inject" % "guice" % "4.0",

  "ch.qos.logback" % "logback-core" % "1.1.3",
  "ch.qos.logback" % "logback-classic" % "1.1.3",

  "org.json" % "json" % "20140107"
)

lazy val root = project.in(file(".")) aggregate(model, rest, crawler)

lazy val model = project.in(file("model"))

lazy val rest = project.in(file("rest")) dependsOn (model % "test->test;compile->compile")

lazy val crawler = project.in(file("crawler")) dependsOn (model % "test->test;compile->compile")

assemblyMergeStrategy in assembly := {
//  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
//  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "logback.xml"                                 => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
