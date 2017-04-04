import org.scoverage.coveralls.Imports.CoverallsKeys._
import sbt.Keys.scalacOptions
import org.scoverage.coveralls.Imports.CoverallsKeys._
import com.typesafe.sbt.SbtScalariform._
import xerial.sbt.Sonatype._
import org.scoverage.coveralls.Imports.CoverallsKeys._
import scalariform.formatter.preferences._

name := "soheila-cms"

description := "CMS"

lazy val commonSettings = Seq(
  organization := "org.soheila",
  version := "0.1.0",
  scalaVersion := "2.11.8",
  aggregate in update := false
)

organization := "io.soheila"

homepage := Some(url("http://www.soheila.io/"))

licenses := Seq("Apache2 License" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

version := "0.1.0-alpha1"

scalaVersion := "2.11.8"

aggregate in update := false

resolvers += Resolver.jcenterRepo

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.scalaz" % "scalaz-core_2.11" % "7.2.2",
  "com.cloudinary" %% "cloudinary-scala-play" % "1.1.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.17",
  "com.typesafe.akka" % "akka-stream_2.11" % "2.4.17",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.17",
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.3.8",
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.14",
  "com.github.romix.akka" % "akka-kryo-serialization_2.11" % "0.5.0",
  "com.iheart" % "ficus_2.11" % "1.4.0",
  "org.sangria-graphql" % "sangria_2.11" % "1.0.0", // SimpleFetcherCache does not cache Relation and was fixed in snapshot
  "org.sangria-graphql" % "sangria-play-json_2.11" % "1.0.0",
  "com.paulgoldbaum" %% "scala-influxdb-client" % "0.5.2",
  "net.codingwell" % "scala-guice_2.11" % "4.1.0",
  "io.soheila" % "play-reactivemongo-commons_2.11" % "0.1.0-alpha1",

  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.0.0" % Test,
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % Test,
  "net.codingwell" % "scala-guice_2.11" % "4.1.0" % Test,
  specs2 % Test,
  "org.specs2" % "specs2-matcher-extra_2.11" % "3.8.5" % Test,
  "org.specs2" % "specs2-mock_2.11" % "3.8.5" % Test,
  "org.specs2" %% "specs2-core" % "3.8.6" % "test"
)

libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-simple")) }

javaOptions in Test += "-Dconfig.file=src/test/resources/application.test.conf"

lazy val `soheila-cms` = Project(id = "soheila-cms", base = file("."))
  .enablePlugins(PlayScala, JavaAppPackaging).disablePlugins(PlayLayoutPlugin)
  .settings(commonSettings: _*)

scalacOptions ++= Seq(
  "-language:postfixOps", // See the Scala docs for value scala.language.postfixOps for a discussion
  "-Xlint:-missing-interpolator", // ignore error(warning): possible missing interpolator: detected interpolated identifier
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

scalacOptions in(Compile, doc) ++= Seq(
  "-no-link-warnings" // Suppresses problems with Scaladoc @throws links
)

parallelExecution in Test := false

fork in Test := true

sonatypeSettings

val pom = <scm>
  <url>git@github.com:esfand-r/soheila-cm.git</url>
  <connection>scm:git@github.com:esfand-r/soheila-cm.git</connection>
</scm>
  <developers>
    <developer>
      <id>esfand-r</id>
      <name>Esfandiar Amirrahimi</name>
      <url>http://soheila.io</url>
    </developer>
  </developers>;

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

sources in (Compile,doc) := Seq.empty

pomExtra := pom

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

coverallsToken := sys.env.get("COVERALLS_REPO_TOKEN")

addCommandAlias("build",       ";clean;coverage;test;format;coverageReport")
addCommandAlias("deployBuild", ";clean;coverage;test;format;coverageReport;coverageAggregate;coveralls")
