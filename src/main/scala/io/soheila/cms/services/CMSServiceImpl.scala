package io.soheila.cms.services

import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.persistence.query.Offset
import akka.persistence.query.scaladsl.{ EventsByTagQuery2, ReadJournal }
import akka.stream.Materializer
import akka.util.Timeout
import com.google.inject.Inject
import com.google.inject.name.Named
import io.soheila.cms.entities.{ Media, Story, UserReference }
import io.soheila.cms.types.StoryType
import io.soheila.cms.vos.{ StoryInitiationVO, StoryUpdateVO }
import sangria.marshalling.playJson._
import akka.persistence.query.scaladsl.EventsByTagQuery
import io.soheila.cms.services.stories.fsm._
import io.soheila.cms.services.stories.StoryServiceFactory

import scala.concurrent.{ ExecutionContext, Future }

class CMSServiceImpl @Inject() (implicit
  val materializer: Materializer,
    ec: ExecutionContext,
    system: ActorSystem,
    val readJournal: ReadJournal,
    @Named("StoryAggregateManager") storyManager: ActorRef,
    storyServiceFactory: StoryServiceFactory) extends CMSService {

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  startStoryView

  def startStoryView: Future[Done] = {
    val stream = readJournal match {
      case eventsByTagQuery: EventsByTagQuery =>
        eventsByTagQuery.eventsByTag("StoryEvents", 0L).map(_.event)
      case eventsByTagQuery2: EventsByTagQuery2 =>
        eventsByTagQuery2.eventsByTag("StoryEvents", Offset.sequence(0l)).map(_.event)
      case _ => throw new RuntimeException
    }

    stream.runForeach {
      e => update(e)
    }(materializer)
  }

  def update(event: Any) = {
    event match {
      case Initialize(uuid, story) =>
        storyServiceFactory.getDefaultStoryService.initialize(uuid, story.storyType, Some(story.submitter))
      case Edit(uuid, _) =>
        (storyManager ? GetCurrentData(uuid)).map {
          case story: Story => storyServiceFactory.getDefaultStoryService.findAndUpdateLatest(story)
          case _ => //todo
        }
      case EditMedia(uuid, mediaSet) =>
        storyServiceFactory.getDefaultStoryService.editMedia(uuid, mediaSet)
      case Archive(uuid) =>
        (storyManager ? GetCurrentData(uuid)).map {
          case story: Story => storyServiceFactory.getDefaultStoryService.findAndUpdateLatest(story)
          case _ => //todo
        }
      case Delete(uuid) =>
        (storyManager ? GetCurrentData(uuid)).map {
          case _: Story => storyServiceFactory.getDefaultStoryService.delete(uuid)
          case _ => //todo
        }
      case Publish(uuid) =>
        (storyManager ? GetCurrentData(uuid)).map {
          case story: Story => storyServiceFactory.getPublishedStoryService.findAndUpdateLatest(story, upsert = true)
          case _ => //todo
        }
      case _ =>
    }
  }

  override def initializeStory(storyType: StoryType.Value, user: UserReference)(implicit ec: ExecutionContext): Future[Option[Story]] = {
    val storyCreateVO = StoryInitiationVO(storyType, user)

    (storyManager ? Initialize(storyCreateVO.uuid, storyCreateVO)).map {
      case story: Story => Some(story)
      case e: Exception => throw e
    }
  }

  override def updateStory(storyUpdateVO: StoryUpdateVO)(implicit ec: ExecutionContext): Future[Option[Story]] = {
    (storyManager ? Edit(storyUpdateVO.uuid, storyUpdateVO)).map {
      case story: Story => Some(story)
    }
  }

  override def readStoryByUUID(uuid: String)(implicit ec: ExecutionContext): Future[Option[Story]] = {
    (storyManager ? GetCurrentData(uuid)).map {
      case story: Story => Some(story)
      case _ => throw new RuntimeException("Could not find story by UUID") //todo
    }
  }

  override def editStoryMedia(uuid: String, medias: Set[Media])(implicit ec: ExecutionContext): Future[Boolean] = {
    (storyManager ? EditMedia(uuid, medias)).map {
      case _: Story => true
      case _ => throw new RuntimeException("Could not edit media.") //todo
    }
  }
}
