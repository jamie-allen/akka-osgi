package com.jamieallen.osgi.actor

import akka.actor.Actor

class MyActor extends Actor {
  def receive = {
    case 1 => println("Received 1")
    case 2 => println("Received 2")
    case _ => println("Received something else")
  }
}