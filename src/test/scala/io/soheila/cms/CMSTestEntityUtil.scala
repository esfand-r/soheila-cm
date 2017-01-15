package io.soheila.cms

import java.time.LocalDateTime
import java.util.UUID

import io.soheila.cms.entities.{ Story, UserReference }
import io.soheila.cms.types.StoryType
import io.soheila.commons.entities.Attribute

object CMSTestEntityUtil {
  def createStory(title: String, slug: String, storyType: StoryType.Value, publishedOn: LocalDateTime, tags: Option[Seq[String]] = None, attributes: Option[Seq[Attribute]] = None): Story = {
    val date = LocalDateTime.now()
    val submitter = UserReference(UUID.randomUUID().toString, "test user")
    val authors = Seq(submitter)
    Story(Some(date), Some(date), Some(publishedOn), storyType, Some(title),
      Some(slug), Some(submitter), Some("headline"), Some(authors),
      tags, None, None, None, attributes, None)
  }
}
