package io.soheila.cms.utils.validators

sealed trait StoryCreationFailure
case object EmptyUUID extends StoryCreationFailure
case object EmptyTitle extends StoryCreationFailure
case object EmptyType extends StoryCreationFailure
case object EmptyHeadline extends StoryCreationFailure

