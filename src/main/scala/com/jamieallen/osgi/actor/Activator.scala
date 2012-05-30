package org.jamieallen.osgi.actor

import org.osgi.framework.{ BundleActivator, BundleContext }
import akka.actor.{ ActorSystem, Props }
import com.jamieallen.osgi.actor.MyActor

class Activator extends BundleActivator {

  override def start(context: BundleContext) {
    val system = ActorSystem()
    val myActor = system.actorOf(Props[MyActor])

    Thread.sleep(100)
    myActor ! 2
  }

  override def stop(context: BundleContext) {
    // Write your code here
  }
}
