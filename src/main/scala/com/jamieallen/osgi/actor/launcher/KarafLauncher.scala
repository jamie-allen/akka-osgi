package com.jamieallen.osgi.actor.launcher

import akka.actor.{ Actor, ActorSystem, Props }
import java.util.{ HashMap, ServiceLoader }
import org.osgi.framework.{ Bundle, BundleContext }
import org.osgi.framework.launch.{ Framework, FrameworkFactory }

object KarafLauncher extends App {
  val system1 = ActorSystem()
  val sysman1 = system1.actorOf(Props[ActorMgr])

  Thread.sleep(5000)
  system1.shutdown
}

class ActorMgr extends Actor {
  // By declaring this as an option, I get safe processing throughout 
  // this code as the Uniform Return Type Principle keeps me safe
  var karaf: Option[Framework] = None

  override def preStart = {
    println("ActorMgr about to be started, initializing Karaf framework.")

    val factory: FrameworkFactory = ServiceLoader.load(classOf[FrameworkFactory], getClass.getClassLoader).iterator.next
    karaf = Some(factory.newFramework(new HashMap[String, AnyRef]()))
    karaf map (_.start)

    val bundleContext: Option[BundleContext] = karaf map (_.getBundleContext())

    // We could make this work from command line arguments rather 
    // than a static list, but this is fine for our purposes here.
    val bundlesToInstall = List(
      "file:///Users/jamie/sandbox/akka-osgi/lib/org.scala-ide.scala.library_2.9.2.v20120330-163119-949a4804e4-vfinal.jar",
      "file:///Users/jamie/sandbox/akka-osgi/lib/config-0.4.2-SNAPSHOT.jar",
      "file:///Users/jamie/sandbox/akka-osgi/lib/akka-actor-2.1-SNAPSHOT.jar",
      "file:///Users/jamie/sandbox/akka-osgi/target/scala-2.9.2/akka-osgi-poc_2.9.2-0.1-SNAPSHOT.jar")

    // Install and start each bundle.  Note that mapping over 
    // the BundleContext provides safety if there is none.
    for {
      bc <- bundleContext
      bTI <- bundlesToInstall
    } bc.installBundle(bTI).start

    //    bundleContext map (bc => for (bTI <- bundlesToInstall) bc.installBundle(bTI).start)

    println("Context started, bundles installed and started: ")
    karaf map (_.getBundleContext.getBundles.foreach(println))
  }

  override def postStop = {
    println("Stopping ActorMgr")
    karaf map (_.stop())
    karaf map (_.waitForStop(0))
  }

  def receive = {
    case msg: String => println(msg)
  }
}
