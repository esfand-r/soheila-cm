package io.soheila.cms.entities

import io.soheila.commons.formats.EnumFormat
import play.api.libs.json.Format

object MediaHost extends Enumeration {
  type MediaHost = Value

  val CloudinaryMedia = Value("CloudinaryMedia")
}

object MediaType extends Enumeration {
  type MediaType = Value

  val Video = Value("Video")
  val Image = Value("Image")
  val Audio = Value("Audio")
}

case class Media(mediaHost: MediaHost.Value, url: String, mediaType: MediaType.Value)

object Media {

  import play.api.libs.json.Json

  implicit val mediaHostFormat: Format[MediaHost.Value] = EnumFormat.enumFormat(MediaHost)
  implicit val mediaTypeFormat: Format[MediaType.Value] = EnumFormat.enumFormat(MediaType)
  implicit val jsonFormat = Json.format[Media]
}
