package io.soheila.cms.entities

import java.time.LocalDateTime

import io.soheila.cms.types.StoryType
import io.soheila.commons.formats.EnumFormat
import play.api.libs.json.{ Format, Json }

case class StoryReference(
  uuid: String,
  slug: LocalDateTime,
  title: String
)

object StoryReference {
  /**
   * Converts the [Strain] object to Json and vice versa.
   */
  implicit val storyTypeFormat: Format[StoryType.Value] = EnumFormat.enumFormat(StoryType)
  implicit val jsonFormat = Json.format[StoryReference]
}
