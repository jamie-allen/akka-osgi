# A Proof of Concept for Akka and OSGi

## Overview
A simple test project to test creating an Akka v2.1-SNAPSHOT ActorSystem inside of an OSGi context.  Akka 2.1 will include OSGi support, bundling all of the subprojects.  This project is based on the [sbtosgi-examples](https://github.com/oscarvarto/sbtosgi-examples) project.

## Felix versus Karaf versus Equinox
Initially, I attempted to execute this application in Felix, but ran into issues with the exposing of sun.misc.Unsafe.  It is possible to configure Felix for bootdelegation of the package, but there is something wrong with the way akka.util.Unsafe is exposed at runtime, and I'm not sure how to get around that.  [Gert Vanthienen](https://github.com/gertv) was able to get tests of his pull requests into Akka for OSGi support to work with Karaf and no extra configuration, so I switched to Karaf as well.

For what it's worth, the shell in Karaf is so much nicer than Felix as well.  Felix has no history, no autocomplete and doesn't allow you to edit commands that you mistype.  Karaf has all of these features and that makes it so much nicer to use.  Good grief.

I have not tried this under Equinox.

## Build and Bundle This Project
1. Clone this repo
2. Make sure you're using SBT v0.11.3 (required for the sbtosgi 0.3.0-SNAPSHOT dependency, as it's not cross-built).
3. At the command line at the root of the project, type `sbt update compile osgi-bundle`

## Installing Karaf
This information was shamelessly stolen from the sbtosgi-example project referenced above, though altered to reflect the runtime and requirements of this project.

To download Apache Karaf, visit [this page](http://karaf.apache.org/index/community/download.html) and get the appropriate distribution for your environment.  Untar it somewhere on your local drive, then go to that folder and start Karaf.
```
$ tar -xzf apache-karaf-2.2.7.tar.gz
$ cd apache-karaf-2.2.7
$ java -jar bin/karaf
```
You should see:
```
➜  apache-karaf-2.2.7  bin/karaf 
        __ __                  ____      
       / //_/____ __________ _/ __/      
      / ,<  / __ `/ ___/ __ `/ /_        
     / /| |/ /_/ / /  / /_/ / __/        
    /_/ |_|\__,_/_/   \__,_/_/         

  Apache Karaf (2.2.7)

Hit '<tab>' for a list of available commands
and '[cmd] --help' for help on a specific command.
Hit '<ctrl-d>' or 'osgi:shutdown' to shutdown Karaf.

karaf@root> 
```
For a quick primer on how to use Karaf, [see here](http://karaf.apache.org/manual/latest-2.2.x/quick-start.html).

## Start the OSGi Container and Install the Required Bundles
Before you install this bundle, you'll need to add library bundles for the Scala language library, Akka-Actor and Typesafe Config as dependencies.  See the lib folder for my pre-built artifacts, in case you don't want to build your own.  Note that the Akka bundle is a 2.1-SNAPSHOT as of revision "35aaa220aa0c65333e75a7c199fe9ebc782c1b89" on May 29, 2012, but the dependency on Typesafe Config has been changed to 0.4.2-SNAPSHOT.  Also, the Config is a 0.4.2-SNAPSHOT as of revision "b3ac8d0539d1df60ff3e5daaf5d619411f426f24" on May 24, 2012.  The Scala library is from my Scala IDE Eclipse distribution.

Use `install` to load the bundles into the OSGi runtime, and then `start` them by bundle ID.  If you ever have to uninstall a bundle to replace it with a new version, use `uninstall` and the bundle ID to remove.
```
karaf@root> install file:///<path to akka-osgi project root>/lib/org.scala-ide.scala.compiler_2.9.2.v20120330-163119-949a4804e4-vfinal.jar
Bundle ID: 50
karaf@root> install file:///<path to akka-osgi project root>/lib/config-0.4.2-SNAPSHOT.jar
Bundle ID: 51
karaf@root> start 51
karaf@root> install file:///<path to akka-osgi project root>/lib/akka-actor-2.1-SNAPSHOT.jar
Bundle ID: 52
karaf@root> start 52
karaf@root> install file:///<path to akka-osgi project root>/target/scala-2.9.2/akka-osgi-poc_2.9.2-0.1-SNAPSHOT.jar
Bundle ID: 53
```
The OSGi context should now look like this:
```
karaf@root> list
START LEVEL 100 , List Threshold: 50
   ID   State         Blueprint      Level  Name
[  50] [Installed  ] [            ] [   80] Scala Library for Eclipse (2.9.2.v20120330-163119-949a4804e4-vfinal)
[  51] [Installed  ] [            ] [   80] com.typesafe.config (0.4.2.SNAPSHOT)
[  52] [Installed  ] [            ] [   80] com.typesafe.akka.actor (2.1.0.SNAPSHOT)
[  53] [Installed  ] [            ] [   80] default.Akka OSGi POC (0.1.0.SNAPSHOT)
```
Start the bundles and see if the proof of concept works.
```
karaf@root> start 50
karaf@root> start 51
karaf@root> start 52
karaf@root> start 53
```
If everything has gone correctly, you will see the following output:
```
<lots of configuration printed out, but ignore that>
Received 2
Received something else: foo
```