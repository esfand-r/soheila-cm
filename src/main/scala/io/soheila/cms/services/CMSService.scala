package io.soheila.cms.services

import io.soheila.cms.entities.{ Media, Story, UserReference }
import io.soheila.cms.types.StoryType
import io.soheila.cms.vos.StoryUpdateVO

import scala.concurrent.{ ExecutionContext, Future }

trait CMSService {
  /**
   * Initializes a story. Story can be initialized as long as type is provided.
   * @param storyType type of the story.
   *
   * @return Either an error message or the uuid of the created story.
   */
  def initializeStory(storyType: StoryType.Value, user: UserReference)(implicit ec: ExecutionContext): Future[Option[Story]]

  def updateStory(storyUpdateVO: StoryUpdateVO)(implicit ec: ExecutionContext): Future[Option[Story]]

  def readStoryByUUID(uuid: String)(implicit ec: ExecutionContext): Future[Option[Story]]

  /**
   * Edit Media of a story.
   * @param uuid story uuid.
   * @param medias a set containing media.
   *
   * @return Future boolean indicating operation success or error.
   */
  def editStoryMedia(uuid: String, medias: Set[Media])(implicit ec: ExecutionContext): Future[Boolean]
}
