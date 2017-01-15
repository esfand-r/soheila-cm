package io.soheila.cms.api.graphql

import io.soheila.cms.api.exceptions.CMSAuthorisationException
import io.soheila.cms.entities.{ CMSUserRole, UserReference }
import io.soheila.cms.services.CMSService
import io.soheila.cms.services.stories.StoryServiceFactory

case class CMSSecureContext(user: Option[UserReference], storyServiceFactory: StoryServiceFactory, cMSService: CMSService) {

  def authorised[T](roles: CMSUserRole.Value*)(fn: UserReference => T) = {
    user.fold(throw CMSAuthorisationException("User not found. You do not have permission to do this operation")) {
      user =>
        if (roles.forall(user.roles.contains)) fn(user)
        else throw CMSAuthorisationException(s"You do not have permission to do this operation by the user")
    }
  }
}
