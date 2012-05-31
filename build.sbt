scalaVersion := "2.9.2"

name := "Akka OSGi POC"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

seq(osgiSettings: _*)

resolvers ++= Seq("snapshots" at "http://repo.akka.io/snapshots",
                 "releases"  at "http://repo.akka.io/releases")

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.1-SNAPSHOT",
  "org.osgi" % "org.osgi.core" % "4.2.0" % "provided")

OsgiKeys.bundleActivator := Some("com.jamieallen.osgi.actor.Activator")

OsgiKeys.exportPackage := Seq("com.jamieallen.osgi.actor")