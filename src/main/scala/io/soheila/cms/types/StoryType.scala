package io.soheila.cms.types

import io.soheila.commons.formats.EnumFormat
import play.api.libs.json.Format

object StoryType extends Enumeration {
  type StoryType = Value

  val UnAssigned = Value("UnAssigned")
  val Article = Value("Article")
  val UserPost = Value("UserPost")
  val GuestPost = Value("GuestPost")
  val BasicPage = Value("BasicPage")
  val Research = Value("Research")
  val News = Value("News")

  implicit val storyTypeFormat: Format[StoryType.Value] = EnumFormat.enumFormat(StoryType)
}
