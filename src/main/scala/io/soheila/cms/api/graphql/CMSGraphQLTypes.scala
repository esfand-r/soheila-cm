package io.soheila.cms.api.graphql

import io.soheila.commons.entities.Attribute
import io.soheila.cms.entities._
import io.soheila.cms.types.StoryType
import sangria.schema.{ BooleanType, EnumType, EnumValue, Field, ListType, ObjectType, OptionType, StringType, fields, interfaces }
import sangria.marshalling._

object CMSGraphQLTypes {
  val StoryTypeEnum = EnumType(
    "StoryType",
    Some("Type of the story."),
    List(
      EnumValue(
        "UnAssigned",
        value = StoryType.UnAssigned,
        description = Some("UnAssigned.")
      ),
      EnumValue(
        "Article",
        value = StoryType.Article,
        description = Some("Articles.")
      ),
      EnumValue(
        "Research",
        value = StoryType.Research,
        description = Some("Research.")
      ),
      EnumValue(
        "BasicPage",
        value = StoryType.BasicPage,
        description = Some("BasicPage.")
      ),
      EnumValue(
        "UserPost",
        value = StoryType.UserPost,
        description = Some("UserPost.")
      ),
      EnumValue(
        "GuestPost",
        value = StoryType.GuestPost,
        description = Some("GuestPost.")
      ),
      EnumValue(
        "News",
        value = StoryType.News,
        description = Some("News.")
      )
    )
  )

  val ContentTypeEnum = EnumType(
    "ContentType",
    Some("Type of content."),
    List(
      EnumValue(
        "Html",
        value = ContentType.Html,
        description = Some("HTML content.")
      ),
      EnumValue(
        "Text",
        value = ContentType.Text,
        description = Some("Text content.")
      ),
      EnumValue(
        "Markup",
        value = ContentType.Markup,
        description = Some("Markup Content.")
      ),
      EnumValue(
        "Wysiwyg",
        value = ContentType.Wysiwyg,
        description = Some("Wysiwyg Content.")
      )
    )
  )

  val UserReferenceSchemaType = ObjectType(
    "UserReferenceSchema",
    "UserReference Schema",
    interfaces[Unit, UserReference](),
    fields[Unit, UserReference](
      Field("uuid", StringType, Some("UUID of a user"), resolve = _.value.uuid),
      Field("name", StringType, Some("Full name of a user"), resolve = _.value.name)
    )
  )

  val MediaHostTypeEnum = EnumType(
    "MediaHostType",
    Some("Type of media hosting server."),
    List(
      EnumValue(
        "CloudinaryMedia",
        value = MediaHost.CloudinaryMedia,
        description = Some("Cloudinary Media.")
      )
    )
  )

  val MediaTypeEnum = EnumType(
    "MediaType",
    Some("Type of media."),
    List(
      EnumValue(
        "Video",
        value = MediaType.Video,
        description = Some("Video.")
      ),
      EnumValue(
        "Audio",
        value = MediaType.Audio,
        description = Some("Audio.")
      ),
      EnumValue(
        "Image",
        value = MediaType.Video,
        description = Some("Image.")
      )
    )
  )

  val MediaSchemaType = ObjectType(
    "MediaSchema",
    "Media Schema",
    interfaces[Unit, Media](),
    fields[Unit, Media](
      Field("mediaHost", MediaHostTypeEnum, Some("Type of Media hosting"), resolve = _.value.mediaHost),
      Field("url", StringType, Some("URL of the media"), resolve = _.value.url),
      Field("mediaType", MediaTypeEnum, Some("Media Type"), resolve = _.value.mediaType)
    )
  )

  val ContentSchemaType = ObjectType(
    "ContentSchema",
    "Image Schema",
    interfaces[Unit, Content](),
    fields[Unit, Content](
      Field("rawText", StringType, Some("Raw text of the content"), resolve = _.value.rawText),
      Field("text", StringType, Some("Text of the content. Can include markup or html"), resolve = _.value.text),
      Field("contentType", ContentTypeEnum, Some("Type of the content."), resolve = _.value.contentType)
    )
  )

  val CommentSchemaType = ObjectType(
    "CommentSchema",
    "Comment Schema",
    interfaces[Unit, Comment](),
    fields[Unit, Comment](
      Field("author", UserReferenceSchemaType, Some("Submitter of a story"), resolve = _.value.author),
      Field("comment", StringType, Some("title of a story"), resolve = _.value.comment)
    )
  )

  val AttributeSchemaType = ObjectType(
    "AttributeSchema",
    "Attribute Schema",
    interfaces[Unit, Attribute](),
    fields[Unit, Attribute](
      Field("key", StringType, Some("Dynamic attribute key."), resolve = _.value.key),
      Field("value", ListType(StringType), Some("Dynamic attribute value."), resolve = _.value.value)
    )
  )

  val StoryVerificationSchemaType = ObjectType(
    "StoryVerificationSchema",
    "StoryVerification Schema",
    interfaces[Unit, StoryVerification](),
    fields[Unit, StoryVerification](
      Field("verified", BooleanType, Some("Indicator as whether a story is verified"), resolve = _.value.verified),
      Field("verifier", OptionType(UserReferenceSchemaType), Some("Information of verifier of the story"), resolve = _.value.verifier)
    )
  )

  val StorySchemaType = ObjectType(
    "Story",
    "Story Schema",
    interfaces[Unit, Story](),
    fields[Unit, Story](
      Field("uuid", OptionType(StringType), Some("UUID of a story"), resolve = _.value.uuid),
      Field("createdOn", OptionType(LocalDateTimeType), Some("Story creation datetime"), resolve = _.value.createdOn),
      Field("updatedOn", OptionType(LocalDateTimeType), Some("Story update datetime"), resolve = _.value.updatedOn),
      Field("publishedOn", OptionType(LocalDateTimeType), Some("Story published datetime"), resolve = _.value.publishedOn),
      Field("storyType", StoryTypeEnum, Some("Type of a story"), resolve = _.value.storyType),
      Field("title", OptionType(StringType), Some("title of a story"), resolve = _.value.title),
      Field("slug", OptionType(StringType), Some("url friendly link of a story"), resolve = _.value.slug),
      Field("submitter", OptionType(UserReferenceSchemaType), Some("Submitter of a story"), resolve = _.value.submitter),
      Field("headline", OptionType(StringType), Some("Headline of a story"), resolve = _.value.headline),
      Field("authors", OptionType(ListType(UserReferenceSchemaType)), Some("Authors of a story"), resolve = _.value.authors),
      Field("tags", OptionType(ListType(StringType)), Some("Tags of a story"), resolve = _.value.tags),
      Field("image", OptionType(MediaSchemaType), Some("Main image of a story"), resolve = _.value.mainMedia),
      Field("content", OptionType(ContentSchemaType), Some("Content of a story."), resolve = _.value.content),
      Field("last20Comments", OptionType(ListType(CommentSchemaType)), Some("Content of a story."), resolve = _.value.last20Comments),
      Field("attributes", OptionType(ListType(AttributeSchemaType)), Some("Last 20 comments on a story."), resolve = _.value.attributes),
      Field("storyVerification", OptionType(StoryVerificationSchemaType), Some("Verification of a story."), resolve = _.value.storyVerification),
      Field("mediaSet", ListType(MediaSchemaType), Some("Media set."), resolve = _.value.mediaSet.toList)
    )
  )
}
