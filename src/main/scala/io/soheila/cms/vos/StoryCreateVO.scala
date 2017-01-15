package io.soheila.cms.vos

import java.time.{ Clock, LocalDateTime }

import io.soheila.commons.entities.Attribute
import io.soheila.cms.entities.{ Content, Media, Story, UserReference }
import io.soheila.cms.types.StoryType
import io.soheila.cms.utils.validators._
import io.soheila.cms.utils.slugs.StringImplicits._
import play.api.libs.json.Json

import scala.language.postfixOps
import scalaz.Scalaz._
import scalaz._

case class StoryCreateVO private (
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

object StoryCreateVO extends Validator[StoryCreationFailure] {

  implicit val jsonFormat = Json.format[StoryCreateVO]

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
  ): ValidationNel[StoryCreationFailure, StoryCreateVO] = {
    (checkNonEmpty(EmptyUUID)(uuid) |@| checkNonNull(EmptyType)(storyType)
      |@| checkNonEmpty(EmptyTitle)(title) |@| checkNonEmpty(EmptyHeadline)(headline))((a, b, c, d) =>
        apply(uuid, storyType, title, slug.getOrElse(title.slugify), headline, authors, tags, image, content, attributes, userReference))
  }

  def toStory(storyCreateVO: StoryCreateVO, story: Story): Story = {
    story.copy(
      attributes = storyCreateVO.attributes,
      authors = storyCreateVO.authors,
      content = storyCreateVO.content,
      headline = Some(storyCreateVO.headline),
      mainMedia = storyCreateVO.image,
      storyType = storyCreateVO.storyType,
      tags = storyCreateVO.tags,
      title = Some(storyCreateVO.title),
      slug = Some(storyCreateVO.slug),
      submitter = Some(storyCreateVO.submitter),
      updatedOn = Some(storyCreateVO.updatedOn)
    )
  }
}
