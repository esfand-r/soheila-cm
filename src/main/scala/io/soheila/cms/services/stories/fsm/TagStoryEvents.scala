package io.soheila.cms.services.stories.fsm

import akka.actor.ExtendedActorSystem
import akka.persistence.journal.{ EventAdapter, EventSeq, Tagged }

class TagStoryEvents(system: ExtendedActorSystem) extends EventAdapter {
  override def toJournal(event: Any): Any = event match {
    case e: StoryDomainEvt => Tagged(e, Set("StoryEvents"))
    case _ => event
  }

  override def fromJournal(event: Any, manifest: String): EventSeq =
    EventSeq.single(event) // identity

  override def manifest(event: Any): String = ""

}
