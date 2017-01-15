package io.soheila.cms.entities

case class Comment(author: UserReference, comment: String)

object Comment {
  import play.api.libs.json.Json

  implicit val jsonFormat = Json.format[Comment]
}
