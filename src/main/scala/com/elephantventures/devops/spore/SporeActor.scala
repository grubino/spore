package com.elephantventures.devops.spore

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

/**
  * Created by gregrubino on 3/25/17.
  */
class SporeActor extends Actor with ActorLogging {
  import SporeActor._

  val dispatcherActor: ActorRef = context.actorOf(BuildCommandDispatcher.props, "build")
  private val workingDir = System.getProperty("user.dir")

  override def receive: Receive = {
    case Initialize =>
      dispatcherActor ! BuildCommandDispatcher.Initialize(s"file://$workingDir/echo.echo")
  }
}

object SporeActor {
  val props: Props = Props[SporeActor]
  case object Initialize
}