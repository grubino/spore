package com.elephantventures.devops.spore

import java.net.URL

import akka.actor.{Actor, ActorLogging, Props}


class EchoBuild extends Actor with ActorLogging {

  private val workingDir = System.getProperty("user.dir")

  def receive: Actor.Receive = {
  	case BuildCommandDispatcher.Initialize(text) =>
      log.info(s"EchoBuild_${self.hashCode} - echo building...")
  	  sender() ! BuildCommandDispatcher.ArtifactGenerated(new URL(text).toString)
  }
}

object EchoBuild {
  val props: Props = Props[EchoBuild]
}
