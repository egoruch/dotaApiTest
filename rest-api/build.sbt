name := "rest-api"

organization := "com.eb.dotapulse"

version := "0.2"

scalaVersion in ThisBuild := "2.12.1"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"                   % "2.4.17",
  "com.typesafe.akka"   %% "akka-http"                    % "10.0.5",
  "com.typesafe.akka"   %% "akka-http-spray-json"         % "10.0.5",
  "ch.qos.logback"      %  "logback-classic"              % "1.1.3"
)

mainClass in assembly := Some("com.eb.dotapulse.rest.RestApiServer")