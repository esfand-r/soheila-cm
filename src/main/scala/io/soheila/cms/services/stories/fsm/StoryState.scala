package io.soheila.cms.services.stories.fsm

import akka.persistence.fsm.PersistentFSM.FSMState

/**
 * StoryState base trait. It is sealed for now to take advantage of exhaustive checks during pattern matching.
 * However, sealed trait means it cannot be extended outside this file.
 * todo: Discuss how we want this lib to be extensible.
 * Should we not have it sealed so user can extend it and add new states? How do we want [[StoryFSM]] to be extensible. How do we approach?
 */
sealed trait StoryState extends FSMState

case object Uninitialized extends StoryState {
  override def identifier: String = "Uninitialized"
}

case object Initialized extends StoryState {
  override def identifier: String = "Initialized"
}

case object Edited extends StoryState {
  override def identifier: String = "Edited"
}

case object Published extends StoryState {
  override def identifier: String = "Published"
}

case object Archived extends StoryState {
  override def identifier: String = "Archived"
}

case object Deleted extends StoryState {
  override def identifier: String = "Deleted"
}
