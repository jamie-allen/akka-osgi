package com.jamieallen.osgi.actor

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

import akka.actor.{ ActorSystem, Props, actorRef2Scala }

class Activator extends BundleActivator {
  val system1 = ActorSystem("System1")

  override def start(context: BundleContext) {
    val myActor = system1.actorOf(Props[MyActor])

    Thread.sleep(100)
    myActor ! 2
    myActor ! "foo"
  }

  override def stop(context: BundleContext) {
    system1.shutdown
  }
}
