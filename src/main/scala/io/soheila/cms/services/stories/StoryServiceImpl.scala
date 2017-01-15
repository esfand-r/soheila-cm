package io.soheila.cms.services.stories

import io.soheila.cms.daos.StoryDAO
import io.soheila.cms.entities.{ Media, Story, UserReference }
import io.soheila.cms.services.exceptions.CMSServiceException
import io.soheila.cms.types.StoryType

import scala.concurrent.{ ExecutionContext, Future }

class StoryServiceImpl(val storyDAO: StoryDAO) extends StoryService {

  override def read(uuid: String)(implicit ec: ExecutionContext): Future[Option[Story]] = {
    storyDAO.findByUUID(uuid).map {
      case Left(dAOException) => throw CMSServiceException(dAOException.message, dAOException)
      case Right(s) => s
    }
  }

  override def initialize(uuid: String, storyType: StoryType.Value, userOption: Option[UserReference])(implicit ec: ExecutionContext): Future[Option[Story]] = {
    storyDAO.create(uuid, Story(storyType, userOption)).map {
      case Left(dAOException) => throw CMSServiceException(dAOException.message, dAOException)
      case Right(s) => Some(s)
    }
  }

  override def findByTypeAndSlug(storyType: StoryType.Value, slug: String)(implicit ec: ExecutionContext): Future[Option[Story]] = {
    storyDAO.findByTypeAndSlug(storyType, slug).map {
      case Left(dAOException) => throw CMSServiceException(dAOException.message, dAOException)
      case Right(story) => story
    }
  }

  override def findAndUpdateLatest(story: Story, upsert: Boolean)(implicit ec: ExecutionContext): Future[Option[Story]] = {
    storyDAO.findAndUpdateLatest(story, upsert).map {
      case Left(dAOException) => throw CMSServiceException(dAOException.message, dAOException)
      case Right(s) => s
    }
  }

  override def editMedia(uuid: String, medias: Set[Media])(implicit ec: ExecutionContext): Future[Boolean] = {
    storyDAO.editMedia(uuid, medias).map {
      case Left(dAOException) => throw CMSServiceException(dAOException.message, dAOException)
      case Right(s) => s
    }
  }

  override def delete(uuid: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    storyDAO.delete(uuid).map {
      case Left(dAOException) => throw CMSServiceException(dAOException.message, dAOException)
      case Right(s) => true
    }
  }
}
