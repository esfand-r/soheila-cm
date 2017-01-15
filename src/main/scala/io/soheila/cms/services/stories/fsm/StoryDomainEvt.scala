package io.soheila.cms.services.stories.fsm

import io.soheila.cms.entities.Media
import io.soheila.cms.vos.{ StoryInitiationVO, StoryUpdateVO }

sealed trait StoryDomainEvt {
  def uuid: String
}
case class Stay(override val uuid: String) extends StoryDomainEvt
case class Initialize(override val uuid: String, story: StoryInitiationVO) extends StoryDomainEvt
case class Edit(override val uuid: String, story: StoryUpdateVO) extends StoryDomainEvt
case class Publish(override val uuid: String) extends StoryDomainEvt
case class Delete(override val uuid: String) extends StoryDomainEvt
case class GetCurrentData(override val uuid: String) extends StoryDomainEvt
case class GetState(override val uuid: String) extends StoryDomainEvt
case class EditMedia(override val uuid: String, mediaSet: Set[Media]) extends StoryDomainEvt
case class Archive(override val uuid: String) extends StoryDomainEvt

