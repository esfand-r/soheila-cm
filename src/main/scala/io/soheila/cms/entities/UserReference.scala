package io.soheila.cms.entities

case class UserReference(
  uuid: String,
  name: String,
  roles: Set[CMSUserRole.Value] = Set(CMSUserRole.SimpleUser)
)

object UserReference {
  import play.api.libs.json.Json
  implicit val jsonFormat = Json.format[UserReference]
}
