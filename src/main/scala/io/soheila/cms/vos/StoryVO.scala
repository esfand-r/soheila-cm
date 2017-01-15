package io.soheila.cms.vos

import io.soheila.cms.entities.UserReference
import io.soheila.cms.types.StoryType

trait StoryVO {
  def uuid: String
  def slug: String
  def submitter: UserReference
  def storyType: StoryType.Value
}
