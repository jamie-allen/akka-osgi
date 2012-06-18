package com.jamieallen.osgi.actor.managed

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala

class Activator extends BundleActivator {
  var system1: Option[ActorSystem] = None

  override def start(context: BundleContext) {
    val classLoader = classOf[ActorSystem].getClassLoader
    val config: Config = {
      val config = ConfigFactory.defaultReference(classLoader)
      config.checkValid(ConfigFactory.defaultReference(classLoader), "akka")
      config
    }

    println("Got config: " + config)
    system1 = Some(ActorSystem("System1", config, classLoader))

    val myActor = system1.get.actorOf(Props(new MyActor("FOO")))

    Thread.sleep(100)
    myActor ! 5
    myActor ! "foo"
  }

  override def stop(context: BundleContext) {
    system1 map { _.shutdown }
  }
}
