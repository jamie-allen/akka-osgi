# A Proof of Concept for Akka and OSGi

## Overview
A simple test project to test creating an Akka v2.1-SNAPSHOT ActorSystem inside of an OSGi context.  This project is based on the [sbtosgi-examples](https://github.com/oscarvarto/sbtosgi-examples) project.

## Build and Bundle This Project
1. Clone this repo
2. Make sure you're using SBT v0.11.2 (required for the sbtosgi dependency)
2. At the command line at the root of the project, type "sbt"
3. At the sbt prompt, type "update compile osgi-bundle"

## Start the OSGi Container and Install the Required Bundles

Before you install this bundle, you'll need to add library bundles for the Scala language library, Akka-Actor and Typesafe Config as dependencies.  See the lib folder for my pre-built artifacts, in case you don't want to build your own.  Note that the Akka bundle is a 2.1-SNAPSHOT as of revision "35aaa220aa0c65333e75a7c199fe9ebc782c1b89" on May 29, 2012, but the dependency on Typesafe Config has been changed to 0.4.2-SNAPSHOT.  Also, the Config is a 0.4.2-SNAPSHOT as of revision "b3ac8d0539d1df60ff3e5daaf5d619411f426f24" on May 24, 2012.  The Scala library is from my Scala IDE Eclipse distribution.

How to install them is explained below.  This information is shamelessly stolen from the sbtosgi-example project referenced above, though altered to reflect the requirements of this project.

Download the [Felix Framework Distribution](http://felix.apache.org/site/downloads.cgi) and unzip it somewhere on your local drive.  Go to that folder and start felix.
```
$ tar -xzf org.apache.felix.main.distribution-4.0.2.tar.gz
$ cd felix-framework-4.0.2
$ java -jar bin/felix
```
You should see:
```
____________________________
Welcome to Apache Felix Gogo

g!
```
Note that the g! is the shell prompt.  Every command you see below will include the g!, but that is not part of the command to be entered.  Sorry to say, it has no history nor ability to backspace, so don't bother trying to use your arrow keys with it.  To see what bundles are running:
```
g! lb
START LEVEL 1
   ID|State      |Level|Name
    0|Active     |    0|System Bundle (4.0.2)
    1|Active     |    1|Apache Felix Bundle Repository (1.6.6)
    2|Active     |    1|Apache Felix Gogo Command (0.12.0)
    3|Active     |    1|Apache Felix Gogo Runtime (0.10.0)
    4|Active     |    1|Apache Felix Gogo Shell (0.10.0)
```
Set the initial bundle level to 2, and move the framework level to that level right away.  For more information about bundle start levels, [see here](http://aaronz-sakai.blogspot.com/2009/05/osgi-system-and-bundle-start-levels.html).
```
g! bundlelevel -i 2
g! frameworklevel 2
```
Use `install` to load the bundles into the OSGi runtime, and then `start` them by bundle ID.  If you ever have to uninstall a bundle to replace it with a new version, use `uninstall` and the bundle ID to remove.
```
g! install file:///<path to felix-akka-example code>/lib/org.scala-ide.scala.compiler_2.9.2.v20120330-163119-949a4804e4-vfinal.jar
Bundle ID: 5
g! start 5
g! install file:///<path to felix-akka-example code>/lib/config-0.4.2-SNAPSHOT.jar
Bundle ID: 6
g! start 6
g! install file:///<path to felix-akka-example code>/lib/akka-actor-2.1-SNAPSHOT.jar
Bundle ID: 7
g! start 7
g! install file:///<path to felix-akka-example code>/target/scala-2.9.2/felix-akka-poc_2.9.2-0.1-SNAPSHOT.jar
Bundle ID: 8
g! start 8
```
Starting the Scala bundle will not actually start it, but it will resolve it.  If you look at the status of the bundle, you will see that it is merely `Resolved`, which is fine.  The OSGi context should look like this:
```
g! lb
START LEVEL 1
   ID|State      |Level|Name
    0|Active     |    0|System Bundle (4.0.2)
    1|Active     |    1|Apache Felix Bundle Repository (1.6.6)
    2|Active     |    1|Apache Felix Gogo Command (0.12.0)
    3|Active     |    1|Apache Felix Gogo Runtime (0.10.0)
    4|Active     |    1|Apache Felix Gogo Shell (0.10.0)
    5|Resolved   |    2|Scala Library for Eclipse (2.9.2.v20120330-163119-949a4804e4-vfinal)
    6|Active     |    2|com.typesafe.config (0.4.2.SNAPSHOT)
    7|Active     |    2|com.typesafe.akka.actor (2.1.0.SNAPSHOT)
    8|Active     |    2|default.Felix Akka POC (0.1.0.SNAPSHOT)
```
That's it.  If everything has gone correctly, you will see that the MyActor instance has received two messages with values of "2" and "foo".