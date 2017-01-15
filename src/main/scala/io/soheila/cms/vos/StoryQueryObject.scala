package io.soheila.cms.vos

import java.util.Locale

import io.soheila.cms.entities.UserReference
import io.soheila.cms.types.StoryType.StoryType
import io.soheila.commons.entities.Attribute
import io.soheila.commons.geospatials.Coordinate

case class StoryQueryObject(
  title: Option[String],
  storyType: StoryType,
  tags: Option[Set[String]] = None,
  authors: Option[Seq[UserReference]],
  attributes: Option[Seq[Attribute]] = None,
  text: Option[String] = None,
  locations: Option[Seq[Coordinate]],
  language: String = Locale.getDefault.getLanguage,
  published: Option[Boolean] = None,
  archived: Option[Boolean] = None
)
