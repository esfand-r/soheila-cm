package io.soheila.cms.vos

import com.fasterxml.uuid.Generators
import io.soheila.cms.entities.UserReference
import io.soheila.cms.types.StoryType
import io.soheila.cms.utils.slugs.StringImplicits._
import scala.language.postfixOps

case class StoryInitiationVO(override val uuid: String, override val slug: String, override val storyType: StoryType.Value, override val submitter: UserReference) extends StoryVO

object StoryInitiationVO {
  def apply(storyType: StoryType.Value, userReference: UserReference): StoryInitiationVO = {
    apply(Generators.timeBasedGenerator().generate().toString, "" slugify, storyType, userReference)
  }

  def apply(storyType: StoryType.Value, slug: String, userReference: UserReference): StoryInitiationVO = {
    apply(Generators.timeBasedGenerator().generate().toString, slug, storyType, userReference)
  }
}
