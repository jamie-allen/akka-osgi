# Overview
A simple test project to test creating an Akka v2.1-SNAPSHOT ActorSystem inside of an OSGi context.  This project is based on the [sbtosgi-examples](https://github.com/oscarvarto/sbtosgi-examples) project.

# How to Run
1. Clone this repo
2. Make sure you're using SBT v0.11.2 (required for the sbtosgi dependency)
2. At the command line at the root of the project, type "sbt"
3. At the sbt prompt, type "update compile osgi-bundle"

# Starting the OSGi Container and Installing This Test

Before you install this bundle, you'll need to add library bundles for Akka and Typesafe Config dependencies.  See the lib folder for my pre-built artifacts, in case you don't want to build your own.  Note that the Akka bundle is a 2.1-SNAPSHOT as of revision "35aaa220aa0c65333e75a7c199fe9ebc782c1b89" on May 29, 2012, but the dependency on Typesafe Config has been changed to 0.4.2-SNAPSHOT.  Also, the Config is a 0.4.2-SNAPSHOT as of revision "b3ac8d0539d1df60ff3e5daaf5d619411f426f24" on May 24, 2012.  

How to install them is explained below.  This information is shamelessly stolen from the sbtosgi-example project referenced above.

Download the [Felix Framework Distribution](http://felix.apache.org/site/downloads.cgi), and uncompress it somewhere you decide (let's say, in `~/myOsgiProjects`). Next, cd into the created directory and start felix.

```
$ tar -xzf org.apache.felix.main.distribution-4.0.2.tar.gz
$ cd felix-framework-4.0.2
$ java -jar bin/felix
```
You'll see

```
____________________________
Welcome to Apache Felix Gogo

g!
```
Let's check what's running on our newly installed framework:

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

Next we set the initial bundle level to 2, and move the framework level to that level right away

```
g! bundlelevel -i 2
g! frameworklevel 2
```

Then we use the `install` command to install the bundles and start it.

```
g! install file:///<path to felix-akka-example code>/lib/config-0.4.2-SNAPSHOT.jar
Bundle ID: 5
g! start 5
g! install file:///<path to felix-akka-example code>/lib/akka-actor-2.1-SNAPSHOT.jar
Bundle ID: 6
g! start 6
g! install file:///<path to felix-akka-example code>/target/scala-2.9.2/felix-akka-poc_2.9.2-0.1-SNAPSHOT.jar
Bundle ID: 7
g! start 7
