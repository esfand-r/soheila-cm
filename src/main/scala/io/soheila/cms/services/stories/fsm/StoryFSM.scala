package io.soheila.cms.services.stories.fsm

import akka.actor.{ Actor, Props }
import akka.persistence.fsm.{ LoggingPersistentFSM, PersistentFSM }
import io.soheila.cms.entities.Story
import io.soheila.cms.services.stories.fsm.StoryAggregateManager._
import io.soheila.cms.vos.StoryUpdateVO

import scala.language.postfixOps
import scala.reflect.{ ClassTag, classTag }

object StoryFSM {
  def props(): Props = Props(new StoryFSM())
}

class StoryFSM extends Actor with PersistentFSM[StoryState, Story, StoryDomainEvt] with LoggingPersistentFSM[StoryState, Story, StoryDomainEvt] {
  override def applyEvent(domainEvent: StoryDomainEvt, currentData: Story): Story = {
    domainEvent match {
      case Initialize(uuid, story) => Story(uuid, story.slug, story.storyType, Some(story.submitter))
      case Edit(_, storyUpdateVO) => StoryUpdateVO.toStory(storyUpdateVO, currentData)
      case EditMedia(_, mediaSet) => currentData.copy(mediaSet = currentData.mediaSet union mediaSet)
      case Archive(_) => currentData.copy(archived = true)
      case Delete(_) => stateData
      case Publish(_) => currentData.copy(published = true)
      case _ => stateData
    }
  }

  override def persistenceId: String = self.path.name

  override def domainEventClassTag: ClassTag[StoryDomainEvt] = classTag[StoryDomainEvt]

  startWith(Uninitialized, Story())

  when(Uninitialized) {
    case Event(Initialize(uuid, story), _) =>
      goto(Initialized) applying Initialize(uuid, story) andThen (_ => sender ! stateData)
    case Event(Edit(uuid, storyUpdateVO), _) =>
      goto(Edited) applying Edit(uuid, storyUpdateVO) andThen (_ => sender ! stateData)
  }

  when(Initialized) {
    case Event(Initialize(_, _), _) => stay
    case Event(Edit(uuid, storyUpdateVO), _) =>
      goto(Edited) applying Edit(uuid, storyUpdateVO) andThen (_ => sender ! stateData)
    case Event(Delete(uuid), _) => goto(Deleted) applying Delete(uuid) andThen (_ => sender ! true)
    case Event(EditMedia(uuid, mediaSet), _) => goto(Edited) applying EditMedia(uuid, mediaSet) andThen (_ => sender ! stateData)
  }

  when(Edited) {
    case Event(Edit(uuid, storyUpdateVO), _) => stay() applying Edit(uuid, storyUpdateVO) andThen (_ => sender ! stateData)
    case Event(EditMedia(uuid, mediaSet), _) => stay() applying EditMedia(uuid, mediaSet) andThen (_ => sender ! stateData)
    case Event(Archive(uuid), _) => goto(Archived) applying Archive(uuid) andThen (_ => sender ! true)
    case Event(Publish(uuid), _) => goto(Published) applying Publish(uuid) andThen (_ => sender ! true)
  }

  when(Archived) {
    case Event(Delete(uuid), _) => goto(Deleted) applying Delete(uuid) andThen (_ => sender ! true)
    case Event(Archive(_), _) => stay andThen (_ => sender ! true)
    case Event(Edit(uuid, storyUpdateVO), _) => goto(Edited) applying Edit(uuid, storyUpdateVO) andThen (_ => sender ! stateData)
    case Event(EditMedia(uuid, mediaSet), _) => goto(Edited) applying EditMedia(uuid, mediaSet) andThen (_ => sender ! stateData)
  }

  when(Published) {
    case Event(Archive(_), _) => stay andThen (_ => sender ! true)
    case Event(Delete(uuid), _) => goto(Deleted) applying Delete(uuid) andThen (_ => sender ! true)
    case Event(Edit(uuid, storyUpdateVO), _) => goto(Edited) applying Edit(uuid, storyUpdateVO) andThen (_ => sender ! stateData)
    case Event(EditMedia(uuid, mediaSet), _) => goto(Edited) applying EditMedia(uuid, mediaSet) andThen (_ => sender ! stateData)
  }

  when(Deleted) {
    // We cannot go anywhere from a Deleted state.
    case _ => stay andThen (_ => sender ! true)
  }

  whenUnhandled {
    case Event(GetCurrentData(_), _) => stay replying stateData
    case Event(GetState(_), _) => stay replying stateName
  }

  onTransition {
    case (a: StoryState) -> (b: StoryState) =>
      log.info(s"Going from ${a.getClass.getSimpleName} to ${b.getClass.getSimpleName}")
  }

  onTermination {
    case StopEvent(PersistentFSM.Failure(_), state, data) =>
      val lastEvents = getLog.mkString("\n\t")
      log.warning("Failure in state " + state + " with data " + data + "\n" +
        "Events leading up to this point:\n\t" + lastEvents)
  }
}
