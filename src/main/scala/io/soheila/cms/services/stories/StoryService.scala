package io.soheila.cms.services.stories

import io.soheila.cms.entities.{ Media, Story, UserReference }
import io.soheila.cms.types.StoryType

import scala.concurrent.{ ExecutionContext, Future }

trait StoryService {
  /**
   * ,ø
   * Reads a story by UUID
   *
   * @param uuid  uuid of the story.
   *
   * @return Some(Entity) if found and None if there was no entity with the given ID.
   */
  def read(uuid: String)(implicit ec: ExecutionContext): Future[Option[Story]]

  /**
   * Initializes a story. Story can be initialized as long as type is provided.
   * @param storyType type of the story.
   * @param user submitter of the story.
   *
   * @return Either an error message or the uuid of the created story.
   */
  def initialize(uuid: String, storyType: StoryType.Value, user: Option[UserReference])(implicit ec: ExecutionContext): Future[Option[Story]]

  /**
   * Returns latest stories by type of story.
   *
   * @param slug slug of a story.
   *
   * @return a story matching type and slug.
   */
  def findByTypeAndSlug(storyType: StoryType.Value, slug: String)(implicit ec: ExecutionContext): Future[Option[Story]]

  /**
   * Finds and updates a story.
   * Note: Stories are initially created automatically when story is initiated.
   *
   * @param story to update.
   *
   * @return updated story.
   */
  def findAndUpdateLatest(story: Story, upsert: Boolean = false)(implicit ec: ExecutionContext): Future[Option[Story]]

  /**
   * Edit Media of a story.
   * @param uuid story uuid.
   * @param medias a set containing media.
   *
   * @return Future boolean indicating operation success or error.
   */
  def editMedia(uuid: String, medias: Set[Media])(implicit ec: ExecutionContext): Future[Boolean]

  /**
   * Deletes a story.
   * @param uuid story uuid.
   *
   * @return Future boolean indicating operation success or error.
   */
  def delete(uuid: String)(implicit ec: ExecutionContext): Future[Boolean]
}
