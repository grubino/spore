package com.elephantventures.devops.spore

import akka.actor.ActorSystem

object ApplicationMain extends App {
  val system = ActorSystem("spore")
  val sporeActor = system.actorOf(SporeActor.props, "spore")

  sporeActor ! SporeActor.Initialize

  system.awaitTermination()
}