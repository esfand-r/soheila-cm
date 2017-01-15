package io.soheila.cms.entities

import play.api.libs.json.{ Format, JsString, JsSuccess, JsValue }

object CMSUserRole extends Enumeration {
  type UserRoles = Value
  val Guest, CMS_Admin, SimpleUser = Value

  implicit val rolesEnumFormat = new Format[CMSUserRole.Value] {
    def reads(json: JsValue) = JsSuccess(CMSUserRole.withName(json.as[String]))
    def writes(role: CMSUserRole.Value) = JsString(role.toString)
  }
}

