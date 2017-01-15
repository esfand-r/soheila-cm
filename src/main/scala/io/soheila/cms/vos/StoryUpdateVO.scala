package io.soheila.cms.vos

import java.time.{ Clock, LocalDateTime }

import io.soheila.cms.entities.{ Content, Media, Story, UserReference }
import io.soheila.cms.types.StoryType
import io.soheila.cms.utils.validators._
import io.soheila.commons.entities.Attribute
import play.api.libs.json.{ Json, OFormat }

import scalaz.Scalaz._
import scalaz._
import io.soheila.cms.utils.slugs.StringImplicits._

case class StoryUpdateVO private (
  override val uuid: String,
  override val storyType: StoryType.Value,
  title: String,
  override val slug: String = "" slugify,
  headline: String,
  authors: Option[Seq[UserReference]] = None,
  tags: Option[Seq[String]] = None,
  image: Option[Media] = None,
  content: Option[Content] = None,
  attributes: Option[Seq[Attribute]] = None,
  override val submitter: UserReference,
  updatedOn: LocalDateTime = LocalDateTime.now(Clock.systemUTC())
) extends StoryVO

object StoryUpdateVO extends Validator[StoryCreationFailure] {

  implicit val jsonFormat: OFormat[StoryUpdateVO] = Json.format[StoryUpdateVO]

  def validated(
    uuid: String,
    storyType: StoryType.Value,
    title: String,
    slug: Option[String],
    headline: String,
    authors: Option[Seq[UserReference]],
    tags: Option[Seq[String]],
    image: Option[Media],
    content: Option[Content],
    attributes: Option[Seq[Attribute]],
    userReference: UserReference
  ): ValidationNel[StoryCreationFailure, StoryUpdateVO] = {
    (checkNonEmpty(EmptyUUID)(uuid) |@| checkNonNull(EmptyType)(storyType)
      |@| checkNonEmpty(EmptyTitle)(title) |@| checkNonEmpty(EmptyHeadline)(headline))((a, b, c, d) =>
        apply(uuid, storyType, title, slug.getOrElse(title.slugify), headline, authors, tags, image, content, attributes, userReference))
  }

  def toStory(storyUpdateVO: StoryUpdateVO, story: Story): Story = {
    story.copy(
      attributes = storyUpdateVO.attributes,
      authors = storyUpdateVO.authors,
      content = storyUpdateVO.content,
      headline = Some(storyUpdateVO.headline),
      mainMedia = storyUpdateVO.image,
      storyType = storyUpdateVO.storyType,
      tags = storyUpdateVO.tags,
      title = Some(storyUpdateVO.title),
      slug = Some(storyUpdateVO.slug),
      submitter = Some(storyUpdateVO.submitter),
      updatedOn = Some(storyUpdateVO.updatedOn)
    )
  }
}
