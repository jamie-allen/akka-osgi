package com.jamieallen.osgi.actor.managed

import akka.actor.Actor

class MyActor(val foo: String) extends Actor {
  def receive = {
    case 1 => println("Received 1 for " + foo)
    case 2 => println("Received 2 for " + foo)
    case x => println("Received something else: " + x + " for " + foo)
  }
}