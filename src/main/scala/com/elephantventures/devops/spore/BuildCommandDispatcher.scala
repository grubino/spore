package com.elephantventures.devops.spore

import java.net.URL

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

class BuildCommandDispatcher extends Actor with ActorLogging {
  import BuildCommandDispatcher._

  var counter = 0
  private val buildActorPropsMap = Map("echo" -> EchoBuild.props)
  private val config = ConfigFactory.load()
  private val buildTypes = (for {
    buildType <- config.getConfigList("spore.buildTypes").asScala.map { cfg =>
      val sporeName = cfg.getString("name")
      val sporeActor = context.actorOf(buildActorPropsMap(sporeName), sporeName)
      sporeName -> sporeActor
    }
  } yield buildType).toMap

  private def _buildSelect(buildFileName: String): ActorRef = buildTypes(buildFileName.split("\\.").reverse.head)
  private def _getBuildFileName(url: URL) = url.getFile.split("/").reverse.head

  override def receive: Actor.Receive = {
  	case Initialize(url) =>

      val _url = new URL(url) // all of the verification, none of the sticky mess of Java!
      val _buildFile = _getBuildFileName(_url)
      val interpretCommandActor = _buildSelect(_buildFile)

	    log.info(s"BuildCommandDispatcher_${self.hashCode} - starting the build command interpreter: ${_buildFile}")
  	  interpretCommandActor ! Initialize(url)

  	case ArtifactGenerated(url) =>
  	  log.info(s"BuildCommandDispatcher_${self.hashCode} - received build complete message #$counter: $url")
  	  counter += 1
  	  context.parent ! ArtifactGenerated(url)
  }
}

object BuildCommandDispatcher {
  val props: Props = Props[BuildCommandDispatcher]
  case class Initialize(buildFileUrl: String)
  case class ArtifactGenerated(artifactUrl: String)
  case class BuildError(message: String)
}