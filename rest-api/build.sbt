name := "rest-api"

version := "0.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"                   % "2.4.17",
  "com.typesafe.akka"   %% "akka-http"                    % "10.0.5",
  "com.typesafe.akka"   %% "akka-http-spray-json"         % "10.0.5",

  "com.typesafe.akka"   %% "akka-slf4j"                   % "2.4.17",
  "ch.qos.logback"      %  "logback-classic"              % "1.1.3",

  "com.typesafe.akka"   %% "akka-http-testkit"            % "10.0.5"  % "test",
  "org.scalatest"       %% "scalatest"                    % "3.0.1"   % "test"
)

mainClass in assembly := Some("com.eb.dotapulse.rest.RestApiServer")
