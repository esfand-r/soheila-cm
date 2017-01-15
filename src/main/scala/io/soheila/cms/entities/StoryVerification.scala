package io.soheila.cms.entities

import play.api.libs.json.Json

case class StoryVerification(verified: Boolean, verifier: Option[UserReference])

object StoryVerification {
  implicit val jsonFormat = Json.format[StoryVerification]
}
