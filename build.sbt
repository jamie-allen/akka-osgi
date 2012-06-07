scalaVersion := "2.9.2"

name := "Akka OSGi POC"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

seq(osgiSettings: _*)

resolvers ++= Seq("ebay open source" at "http://ebayopensource.org/nexus/content/groups/public",
				          "spring" at "http://repository.springsource.com/maven/bundles/release",
                  "snapshots" at "http://repo.akka.io/snapshots",
                  "releases"  at "http://repo.akka.io/releases")

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.1-SNAPSHOT",
  "org.osgi" % "org.osgi.core" % "4.2.0" % "provided",
  "org.apache.karaf" % "apache-karaf" % "2.2.7")

OsgiKeys.bundleActivator := Some("com.jamieallen.osgi.actor.managed.Activator")

OsgiKeys.exportPackage := Seq("com.jamieallen.osgi.actor")