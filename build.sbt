name := "R2D2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.ddahl" %% "rscala" % "2.2.2"

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0"
libraryDependencies += "com.typesafe.play" %% "play-ws-standalone-json" % "1.0.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

libraryDependencies += "com.typesafe" % "config" % "1.3.1"