organization := "bizo.com"

name := "dependency-repository-indexer"

version := "0.0.1"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.6.2",
  "org.mongodb" % "mongo-java-driver" % "2.11.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "junit" % "junit" % "4.10" % "test",
  "com.novocode" % "junit-interface" % "0.10-M4" % "test"
)

EclipseKeys.withSource := true
