package io.soheila.cms.entities

import io.soheila.commons.formats.EnumFormat
import play.api.libs.json.Format

object ContentType extends Enumeration {
  type ContentType = Value

  val Html = Value("Html")
  val Text = Value("Text")
  val Markup = Value("Markup")
  val Wysiwyg = Value("Wysiwyg")
}

case class Content(rawText: String, text: String, contentType: ContentType.Value)

object Content {
  import play.api.libs.json.Json

  implicit val contentTypeFormat: Format[ContentType.Value] = EnumFormat.enumFormat(ContentType)
  implicit val jsonFormat = Json.format[Content]
}
