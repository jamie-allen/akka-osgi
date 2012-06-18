package com.jamieallen.osgi.actor.managed

import akka.actor.Actor
import akka.event.Logging

class MyActor(val foo: String) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case 1 => log.info("Received 1 for " + foo)
    case 2 => log.info("Received 2 for " + foo)
    case x => log.info("Received something else: " + x + " for " + foo)
  }
}