package com.jamieallen.osgi.actor

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import akka.actor.{ ActorSystem, Props, actorRef2Scala }
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

class Activator extends BundleActivator {
  var system1: Option[ActorSystem] = None

  override def start(context: BundleContext) {
    val classLoader = ActorSystem.getClass.getClassLoader
    val config: Config = {
      val config = ConfigFactory.defaultReference(classLoader)
      config.checkValid(ConfigFactory.defaultReference(classLoader), "akka")
      config
    }

    println("Trying AKKA class loader.  Got config: " + config)
    system1 = Some(ActorSystem("System1", config, classLoader))

    val myActor = system1.get.actorOf(Props[MyActor])

    Thread.sleep(100)
    myActor ! 2
    myActor ! "foo"
  }

  override def stop(context: BundleContext) {
    system1 map { _.shutdown }
  }
}
