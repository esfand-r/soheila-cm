package io.soheila.cms.entities

import java.time.{ Clock, LocalDateTime }
import java.util.Locale

import com.fasterxml.uuid.Generators
import io.soheila.cms.types.StoryType
import io.soheila.commons.entities.{ Attribute, IdentityWithAudit }
import io.soheila.cms.utils.slugs.StringImplicits._
import play.api.libs.json.OFormat
import reactivemongo.bson.BSONObjectID

/**
 * The Story object in content management system.
 *
 * @param uuid              The unique UUID of story. It is sequential and based on time.
 * @param createdOn         date/time the story got created.
 * @param updatedOn         date/time the story got updated.
 * @param storyType         Mandatory field specifying the type of a story. @see [[StoryType]]
 * @param title             title of the article.
 * @param slug              url friendly identifier for the article.
 * @param headline          headline of the story.
 * @param submitter         submitter of the story.
 * @param authors           authors of the story.
 * @param tags              tags of a story.
 * @param mainMedia         main image of the story.
 * @param content           content of the story.
 * @param last20Comments    last 20 comments on a story.
 * @param attributes        dynamic user-defined attributes of the story.
 * @param storyVerification indicator whether story is verified or not.
 * @param mediaSet          media files attached to the story.
 * @param language          language of the story. Defaults to JVM default if not provided.
 */
case class Story(
  uuid: Option[String] = Some(Generators.timeBasedGenerator().generate().toString),
  createdOn: Option[LocalDateTime],
  updatedOn: Option[LocalDateTime],
  publishedOn: Option[LocalDateTime],
  storyType: StoryType.Value,
  title: Option[String],
  slug: Option[String],
  headline: Option[String] = None,
  submitter: Option[UserReference] = None,
  authors: Option[Seq[UserReference]],
  tags: Option[Seq[String]] = Some(Seq()),
  mainMedia: Option[Media] = None,
  content: Option[Content] = None,
  last20Comments: Option[List[Comment]] = None,
  attributes: Option[Seq[Attribute]] = None,
  storyVerification: Option[StoryVerification] = Some(StoryVerification(verified = false, None)),
  nonce: String = BSONObjectID.generate().stringify,
  mediaSet: Set[Media] = Set(),
  language: String = Locale.ENGLISH.toLanguageTag,
  published: Boolean = false,
  archived: Boolean = false
)

/**
 * The companion object.
 */
object Story {

  import play.api.libs.json.Json

  implicit val jsonFormat: OFormat[Story] = Json.format[Story]

  implicit object StoryIdentity extends IdentityWithAudit[Story, String] {
    val name = "uuid"

    override def of(entity: Story): Option[String] = entity.uuid

    override def set(entity: Story, uuid: String): Story = entity.copy(uuid = Option(uuid))

    override def clear(entity: Story): Story = entity.copy(uuid = None)

    override def newID: String = Generators.timeBasedGenerator().generate().toString

    override def addAuditTrail(entity: Story): Story = entity.copy(createdOn = Some(LocalDateTime.now(Clock.systemUTC())), updatedOn = Some(LocalDateTime.now(Clock.systemUTC())))

    override def updateAuditTrail(entity: Story): Story = entity.copy(updatedOn = Some(LocalDateTime.now(Clock.systemUTC())))
  }

  def apply(uuid: String, slug: String, storyType: StoryType.Value, userReference: Option[UserReference]): Story = Story(Some(uuid), None, None, None, storyType,
    None, Some(slug), None, userReference, None,
    None, None, None, None, None, None)

  def apply(storyType: StoryType.Value, userReference: Option[UserReference]): Story = Story(Some(Generators.timeBasedGenerator().generate().toString), None, None, None, storyType,
    None, Some("".slugify), None, userReference, None,
    None, None, None, None, None, None)

  def apply(): Story = Story(Some(Generators.timeBasedGenerator().generate().toString), None, None, None, StoryType.UnAssigned,
    None, Some("".slugify), None, None, None,
    None, None, None, None, None, None)

  def apply(
    createdOn: Option[LocalDateTime],
    updatedOn: Option[LocalDateTime],
    publishedOn: Option[LocalDateTime],
    storyType: StoryType.Value,
    title: Option[String],
    slug: Option[String],
    submitter: Option[UserReference],
    headline: Option[String],
    authors: Option[Seq[UserReference]],
    tags: Option[Seq[String]],
    image: Option[Media],
    content: Option[Content],
    last20Comments: Option[List[Comment]],
    attributes: Option[Seq[Attribute]],
    storyVerification: Option[StoryVerification]
  ): Story = apply(
    Some(Generators.timeBasedGenerator().generate().toString),
    createdOn, updatedOn, publishedOn, storyType, title, slug,
    headline, submitter, authors, tags, image, content, last20Comments,
    attributes, storyVerification
  )
}

