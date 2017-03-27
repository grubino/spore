package com.elephantventures.devops.spore

import akka.actor.ActorSystem
import akka.testkit.{ TestKit, ImplicitSender }
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
 
class SporeSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
 
  def this() = this(ActorSystem("SporeSpec"))

  val echoBuildArtifactDir: String = System.getProperty("user.dir")
  val echoBuildArtifact = s"file://$echoBuildArtifactDir/echo.echo"

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Spore actor" must {
    "initialize the Build Dispatcher" in {
      val sporeActor = system.actorOf(SporeActor.props)
      sporeActor ! SporeActor.Initialize
      expectMsg(BuildCommandDispatcher.ArtifactGenerated(echoBuildArtifact))
    }
  }

  "A Build Dispatcher actor" must {
    "dispatch build commands to its children" in {
      val buildDispatcherActor = system.actorOf(BuildCommandDispatcher.props)
      buildDispatcherActor ! BuildCommandDispatcher.Initialize(echoBuildArtifact)
      expectMsg(BuildCommandDispatcher.ArtifactGenerated(echoBuildArtifact))
    }
  }

  "A Echo Build actor" must {
    "send back an Artifact Generated message on a ping" in {
      val pongActor = system.actorOf(EchoBuild.props)
      pongActor ! BuildCommandDispatcher.Initialize(echoBuildArtifact)
      expectMsg(BuildCommandDispatcher.ArtifactGenerated(echoBuildArtifact))
    }
  }

}
