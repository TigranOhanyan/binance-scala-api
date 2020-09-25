organization := "plato.binance"

name := "binance-scala-api"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(

  "com.squareup.retrofit2" % "retrofit" % "2.3.0",

  "commons-codec" % "commons-codec" % "1.10",

  "junit" % "junit" % "4.12" % Test,

  "com.novocode" % "junit-interface" % "0.11" % Test

) ++ Seq("core", "generic", "parser").map(s => "io.circe" %% s"circe-$s" % "0.9.0")

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.2.0"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.8"

assembly / assemblyJarName := "Binance.jar"

assembly / assemblyOption := ( assembly / assemblyOption).value.copy(includeScala = false)
