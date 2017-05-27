name := "grab-oclc"

version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.2",
  "org.jsoup" % "jsoup" % "1.10.2",
  // https://mvnrepository.com/artifact/com.typesafe.play/play-ws_2.11
  "com.typesafe.play" %% "play-ws" % "2.5.15",
  // LOGGING
  "org.slf4s" %% "slf4s-api" % "1.7.12",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  // https://mvnrepository.com/artifact/com.typesafe/config
  "com.typesafe" % "config" % "1.3.1",
  // https://mvnrepository.com/artifact/org.codehaus.plexus/plexus-utils
  "org.codehaus.plexus" % "plexus-utils" % "3.0.24"


  // UNSUSED OLD DEPS
//  "com.typesafe.akka" %% "akka-stream-experimental" % "2.0.3",
//  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.2",
//  "io.spray" %% "spray-client" % "1.3.2",
//  "org.jsoup" % "jsoup" % "1.8.1",
)

enablePlugins(JavaAppPackaging)