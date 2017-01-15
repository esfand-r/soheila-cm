package io.soheila.cms.daos

import io.soheila.commons.crud.CRUDDAO
import io.soheila.commons.entities.Page
import io.soheila.cms.daos.exceptions.DAOException
import io.soheila.cms.entities.{ Media, Story }
import io.soheila.cms.types.StoryType

import scala.concurrent.Future

trait StoryDAO extends CRUDDAO[Story, String] {
  /**
   * Returns latest stories by type of story.
   *
   * @param uuid of a story.
   *
   * @return a story matching type and slug.
   */
  def findByUUID(uuid: String): Future[Either[DAOException, Option[Story]]]

  /**
   * Returns latest stories by type of story.
   *
   * @param slug slug of a story.
   *
   * @return a story matching type and slug.
   */
  def findByTypeAndSlug(storyType: StoryType.Value, slug: String): Future[Either[DAOException, Option[Story]]]

  /**
   * Finds and updates a story.
   * Note: Stories are initially created automatically when story is initiated.
   *
   * @param story to update.
   *
   * @return updated story.
   */
  def findAndUpdateLatest(story: Story, upsert: Boolean = false): Future[Either[DAOException, Option[Story]]]

  /**
   * Updates title of existing story.
   * Note: Stories are initially created automatically when story is initiated.
   *
   * @param uuid uuid of a story.
   * @param title title of a story.
   *
   * @return a story matching type and slug.
   */
  def updateTitle(uuid: String, title: String, currentNonce: String): Future[Either[DAOException, Option[Story]]]

  /**
   * Returns latest stories by type of story.
   *
   * @param limit of records to return.
   * @return latest stories sorted by dat published.
   */
  def findLatestByType(storyType: StoryType.Value, limit: Int = 10): Future[Either[DAOException, List[Story]]]

  /**
   * Returns stories by tag.
   * @param page page number.
   * @param limit of records to return in each page.
   * @param sortFilter  filter to be used for sorting. sortFilter._1 is the name of the filed to be sorted and sortFilter._2 is the sort direction.
   *                    if an option is provided, ._1 must be a notBlank string and ._2 must be either 1 or -1.
   * @return page of found entities with matching tags.
   */
  def findByTags(tags: Seq[String], page: Int = 0, limit: Int, sortFilter: Option[(String, Int)] = None): Future[Either[DAOException, Page[Story]]]

  /**
   * Returns stories by tag.
   * @param page page number.
   * @param limit of records to return in each page.
   * @param sortFilter  filter to be used for sorting. sortFilter._1 is the name of the filed to be sorted and sortFilter._2 is the sort direction.
   *                    if an option is provided, ._1 must be a notBlank string and ._2 must be either 1 or -1.
   * @return page of found entities with matching tags.
   */
  def findByAttributes(tags: Map[String, Seq[String]], page: Int = 0, limit: Int, sortFilter: Option[(String, Int)]): Future[Either[DAOException, Page[Story]]]

  /**
   * Edit Media of a story.
   * @param uuid story uuid.
   * @param medias a set containing media.
   *
   * @return either a boolean indicating operation success or error.
   */
  def editMedia(uuid: String, medias: Set[Media]): Future[Either[DAOException, Boolean]]
}
