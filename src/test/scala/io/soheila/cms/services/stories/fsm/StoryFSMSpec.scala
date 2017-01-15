package io.soheila.cms.services.stories.fsm

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.pattern.ask
import akka.testkit.{ ImplicitSender, TestActorRef, TestKitBase }
import akka.util.Timeout
import io.soheila.cms.entities.{ Story, UserReference }
import io.soheila.cms.types.StoryType
import io.soheila.cms.vos.StoryInitiationVO
import com.typesafe.config.ConfigFactory
import org.specs2.{ Specification, SpecificationLike }
import org.specs2.mock.Mockito
import org.specs2.specification.AfterAll
import play.api.test.WithApplication

import scala.concurrent.duration._
import scala.concurrent.{ Future, _ }

class StoryFSMSpec(implicit val ec: ExecutionContext) extends WithApplication with SpecificationLike with AfterAll
    with Mockito with TestKitBase with ImplicitSender with org.specs2.matcher.MustThrownExpectations {
  implicit val timeout = Timeout(25, TimeUnit.SECONDS)

  lazy val appCfg = ConfigFactory.load("application.test.conf")
  lazy implicit val system = ActorSystem(getClass.getSimpleName, appCfg)
  lazy val log: LoggingAdapter = Logging(system, this.getClass)

  val fsm: TestActorRef[StoryFSM] = TestActorRef(StoryFSM.props())
  val mustBeTypedProperly: TestActorRef[StoryFSM] = fsm
  val underlyingFSM: StoryFSM = fsm.underlyingActor

  def is =
    s2"""
This is a specification to check the story FSM to
   start in Uninitialized state                                                         $startCheck
   go to initialized state after receiving the Initialized Domain Even                  $initialStateCheck
   """

  def startCheck = {
    underlyingFSM.stateName must beEqualTo(Uninitialized)
  }

  def initialStateCheck = {
    val uuid = UUID.randomUUID().toString
    val userReference = UserReference(UUID.randomUUID().toString, "test test")
    val story = StoryInitiationVO(StoryType.Article, userReference)

    val futureStory: Future[Option[Story]] = (fsm ? Initialize(UUID.randomUUID().toString, story)).map {
      case story: Story => Some(story)
    }

    val storyOption: Option[Story] = Await.result(futureStory, 25 seconds)

    storyOption must not beEmpty

    underlyingFSM.stateName must beEqualTo(Initialized)
  }

  override def afterAll() {
    system.terminate()
  }
}
