package com.jamieallen.osgi.actor.launcher

import akka.actor.{ Actor, ActorSystem, Props }
import java.util.{ HashMap, ServiceLoader }
import org.osgi.framework.{ Bundle, BundleContext, BundleException }
import org.osgi.framework.launch.{ Framework, FrameworkFactory }
import akka.event.Logging
import scala.util.control.Exception._

/**
 * This is a simple example of how to use an Akka actor to manage the entire
 * OSGi container, installing and starting bundles and shutting them down.
 */
object KarafLauncher extends App {
  val system1 = ActorSystem()
  val sysman1 = system1.actorOf(Props[ActorMgr])

  Thread.sleep(5000)
  system1.shutdown
}

class ActorMgr extends Actor {
  val log = Logging(context.system, this)

  // By declaring this as an option, I get safe processing throughout this code as the Uniform Return Type 
  // Principle keeps me from having to check whether or not the Framework exists before I want to do something.
  var karaf: Option[Framework] = None

  // Need references to the bundles we've added so we can get rid of them on shutdown.
  var installedBundles: List[Bundle] = List()

  override def preStart = {
    log.debug("ActorMgr about to be started, initializing Karaf framework.")

    // Note that this line, where we get an iterator and call next, drives me crazy.  However, if
    // no factory can be loaded, an exception will be thrown with no next element in the iterator,
    // which kills the actor.  So the behavior is actually what we want.
    val factory: FrameworkFactory = ServiceLoader.load(classOf[FrameworkFactory], getClass.getClassLoader).iterator.next
    karaf = Some(factory.newFramework(new HashMap[String, AnyRef]()))
    karaf map (_.start)

    val bundleContext: Option[BundleContext] = karaf map (_.getBundleContext)

    // Uninstall existing bundles
    bundleContext map (_.getBundles foreach { bundle =>
      log.info(bundle.getSymbolicName)
      bundle.getSymbolicName match {
        case "org.scala-ide.scala.library" | "com.typesafe.config" | "com.typesafe.akka.actor" | "default.Akka OSGi POC" => {
          log.info("Uninstalling bundle: " + bundle.getSymbolicName)
          bundle.uninstall
        }
        case _ =>
      }
      //      ignoring(classOf[BundleException]) { bundle.uninstall }
    })

    // We could make this work from command line arguments rather 
    // than a static list, but this is fine for our purposes here.
    val bundlesToInstall = List(
      "file:///Users/jamie/sandbox/akka-osgi/lib/org.scala-ide.scala.library_2.9.2.v20120330-163119-949a4804e4-vfinal.jar",
      "file:///Users/jamie/sandbox/akka-osgi/lib/config-0.4.2-SNAPSHOT.jar",
      "file:///Users/jamie/sandbox/akka-osgi/lib/akka-actor-2.1-SNAPSHOT.jar",
      "file:///Users/jamie/sandbox/akka-osgi/target/scala-2.9.2/akka-osgi-poc_2.9.2-0.1-SNAPSHOT.jar")

    // Install and start each bundle; the for comprehension over the Option[BundleContext] provides safety - if none
    // exists, no bundles will be yielded into the list of installedBundles, and therefore none will be started next.
    installedBundles = for {
      bTI <- bundlesToInstall
      bc <- bundleContext
    } yield bc.installBundle(bTI)

    installedBundles map (_.start)

    log.debug("Context started, bundles installed and started: ")
    karaf map (_.getBundleContext.getBundles.foreach(b => log.debug(b.getSymbolicName)))
  }

  override def postStop = {
    log.debug("Stopping ActorMgr")
    karaf map (_.stop())
    karaf map (_.waitForStop(0))
  }

  def receive = {
    case msg: String => log.info(msg)
  }
}
