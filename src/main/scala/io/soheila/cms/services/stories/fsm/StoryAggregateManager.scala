package io.soheila.cms.services.stories.fsm

import akka.actor.Props
import io.soheila.cms.services.stories.StoryService

object StoryAggregateManager {
  val Name = "StoryAggregateManager"
  // event

  def props(storyService: StoryService): Props = Props(new StoryAggregateManager())
}

class StoryAggregateManager extends AggregateManager {

  def processCommand = {
    case domainEvent: StoryDomainEvt => processAggregateCommand(domainEvent.uuid, domainEvent)
  }

  def processAggregateCommand(aggregateId: String, domainEvent: StoryDomainEvt) = {
    val maybeChild = context child s"story-$aggregateId"
    maybeChild match {
      case Some(child) =>
        child forward domainEvent
      case None =>
        val child = create(aggregateId)
        child forward domainEvent
    }
  }

  def create(uuid: String) = {
    val agg = context.actorOf(aggregateProps(), s"story-$uuid")
    context watch agg
    println(agg)
    agg
  }

  override def aggregateProps() = StoryFSM.props()

}
