package io.soheila.cms.daos

import java.time.LocalDateTime

import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import io.soheila.cms.{ CMSTestEntityUtil, MongoScope, MongoSpecification, WithMongo }
import io.soheila.cms.entities.{ Media, MediaHost, MediaType, Story }
import io.soheila.cms.types.StoryType
import io.soheila.commons.entities.{ Attribute, Page }
import io.soheila.commons.exceptions.ErrorCode
import play.api.libs.json.Json
import play.api.test.{ PlaySpecification, WithServer }

import scala.concurrent.ExecutionContext

class StoryDAOSpec(implicit val ec: ExecutionContext) extends PlaySpecification with MongoSpecification {

  "The `create` method" should {
    "Fail when a duplicate slug for the same StoryType is inserted" in new WithMongo with Context {

      val story = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, LocalDateTime.now())

      await(storyDao.create(story))

      val story2 = CMSTestEntityUtil.createStory("title2", "title", StoryType.Article, LocalDateTime.now())

      val error = await(storyDao.create(story2)).left.get
      error.error must be equalTo ErrorCode.DUPLICATE_ENTITY

      val story3 = CMSTestEntityUtil.createStory("title2", "title", StoryType.Research, LocalDateTime.now())

      // Check to see if we could insert the same slug for another type of story.
      await(storyDao.create(story3)).right.get must haveClass[Story]

    }
  }

  "The `updateTitle` method" should {
    "Update title and nonce of the article" in new WithMongo with Context {

      val storyToCreate = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, LocalDateTime.now())
      val createdStory = await(storyDao.create(storyToCreate)).right.get

      val updatedStory = await(storyDao.updateTitle(createdStory.uuid.get, "title2", createdStory.nonce)).right.get.get

      updatedStory.title.get must beEqualTo("title2")
      updatedStory.nonce must not be createdStory.nonce
    }
  }

  "The `findLatestByType` method" should {
    "should find the latest published" in new WithMongo with Context {

      val date1: LocalDateTime = LocalDateTime.of(2016, 1, 15, 12, 0, 0)
      val story1 = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, date1)

      val date2: LocalDateTime = LocalDateTime.of(2016, 1, 15, 13, 0, 0)
      val story2 = CMSTestEntityUtil.createStory("title2", "title2", StoryType.Article, date2)

      val date3: LocalDateTime = LocalDateTime.of(2016, 1, 15, 14, 0, 0)
      val story3 = CMSTestEntityUtil.createStory("title3", "title3", StoryType.Article, date3)

      val date4: LocalDateTime = LocalDateTime.of(2016, 1, 15, 15, 0, 0)
      val story4 = CMSTestEntityUtil.createStory("title4", "title4", StoryType.Article, date4)

      val date5: LocalDateTime = LocalDateTime.of(2016, 1, 15, 16, 0, 0)
      val story5 = CMSTestEntityUtil.createStory("title5", "title5", StoryType.Article, date5)

      val date6: LocalDateTime = LocalDateTime.of(2016, 1, 15, 17, 0, 0)
      val story6 = CMSTestEntityUtil.createStory("title6", "title6", StoryType.Research, date6)

      await(storyDao.create(story6))
      await(storyDao.create(story4))
      await(storyDao.create(story2))
      await(storyDao.create(story1))
      await(storyDao.create(story3))
      await(storyDao.create(story5))

      val articlesSortedFromNewest: List[Story] = await(storyDao.findLatestByType(StoryType.Article, 10)).right.get

      articlesSortedFromNewest.map(s => s.publishedOn.get) must_== List(date5, date4, date3, date2, date1)
      articlesSortedFromNewest.size must beEqualTo(5)
    }
  }

  "The `findByTags` method" should {
    val date1: LocalDateTime = LocalDateTime.of(2016, 1, 15, 12, 0, 0)
    val story1 = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, date1, Some(Seq("tagA", "tagB")))

    val date2: LocalDateTime = LocalDateTime.of(2016, 1, 15, 13, 0, 0)
    val story2 = CMSTestEntityUtil.createStory("title2", "title2", StoryType.Article, date2, Some(Seq("tagA", "tagB")))

    val date3: LocalDateTime = LocalDateTime.of(2016, 1, 15, 14, 0, 0)
    val story3 = CMSTestEntityUtil.createStory("title3", "title3", StoryType.Article, date3, Some(Seq("tagA", "tagB", "tagC")))

    val date4: LocalDateTime = LocalDateTime.of(2016, 1, 15, 15, 0, 0)
    val story4 = CMSTestEntityUtil.createStory("title4", "title4", StoryType.Article, date4, Some(Seq("tagA")))

    val date5: LocalDateTime = LocalDateTime.of(2016, 1, 15, 16, 0, 0)
    val story5 = CMSTestEntityUtil.createStory("title5", "title5", StoryType.Article, date5, Some(Seq()))

    val date6: LocalDateTime = LocalDateTime.of(2016, 1, 15, 17, 0, 0)
    val story6 = CMSTestEntityUtil.createStory("title6", "title6", StoryType.Research, date6, Some(Seq("tagD", "tagE")))

    "should find paginated stories by tags" in new WithMongo with Context {
      await(storyDao.create(story6))
      await(storyDao.create(story4))
      await(storyDao.create(story2))
      await(storyDao.create(story1))
      await(storyDao.create(story3))
      await(storyDao.create(story5))

      val articlesByTag: Page[Story] = await(storyDao.findByTags(Seq("tagA", "tagB"), 0, 3, None)).right.get
      articlesByTag.items.size must beEqualTo(3)
      articlesByTag.total must beEqualTo(6)

      val tagsOfFoundArticles: Seq[String] = articlesByTag.items.flatMap(item => item.tags.get)

      tagsOfFoundArticles must contain("tagA", "tagB")
      tagsOfFoundArticles must not contain "tagC" and not contain "tagD" and not contain "tagDE"
    }
  }

  "The `findByAttributes` method" should {
    val date1: LocalDateTime = LocalDateTime.of(2016, 1, 15, 12, 0, 0)
    val story1 = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, date1, None,
      Some(Seq(Attribute("testKey1", Seq("testValue1A")))))

    val date2: LocalDateTime = LocalDateTime.of(2016, 1, 15, 13, 0, 0)
    val story2 = CMSTestEntityUtil.createStory("title2", "title2", StoryType.Article, date2, None)

    val date3: LocalDateTime = LocalDateTime.of(2016, 1, 15, 14, 0, 0)
    val story3 = CMSTestEntityUtil.createStory("title3", "title3", StoryType.Article, date3, None)

    val date4: LocalDateTime = LocalDateTime.of(2016, 1, 15, 15, 0, 0)
    val story4 = CMSTestEntityUtil.createStory("title4", "title4", StoryType.Article, date4, None)

    val date5: LocalDateTime = LocalDateTime.of(2016, 1, 15, 16, 0, 0)
    val story5 = CMSTestEntityUtil.createStory("title5", "title5", StoryType.Article, date5, None)

    val date6: LocalDateTime = LocalDateTime.of(2016, 1, 15, 17, 0, 0)
    val story6 = CMSTestEntityUtil.createStory("title6", "title6", StoryType.Research, date6, None,
      Some(Seq(Attribute("testKey1", Seq("testValue1A", "testValue6B")))))

    "should find paginated stories by dynamic attribute with exact match on the values" in new WithMongo with Context {
      await(storyDao.create(story6))
      await(storyDao.create(story4))
      await(storyDao.create(story2))
      await(storyDao.create(story1))
      await(storyDao.create(story3))
      await(storyDao.create(story5))

      val articlesByAttributeOneExisting: Page[Story] = await(storyDao.findByAttributes(Map(("testKey1", Seq("testValue1A"))), 0, 3, None)).right.get
      articlesByAttributeOneExisting.items.size must beEqualTo(1)

      // Should return all that have the values for the given key
      val articlesByAttributeOneTagMatch: Page[Story] = await(storyDao.findByAttributes(Map(("testKey1", Seq("testValue1A", "testValue6B"))), 0, 3, None)).right.get
      articlesByAttributeOneTagMatch.items.size must beEqualTo(1)
    }

    "should not find any stories when there is no match on the specified key" in new WithMongo with Context {
      await(storyDao.create(story6))
      await(storyDao.create(story4))
      await(storyDao.create(story2))
      await(storyDao.create(story1))
      await(storyDao.create(story3))
      await(storyDao.create(story5))

      val articlesByAttributeNonExisting: Page[Story] = await(storyDao.findByAttributes(Map(("testKey", Seq("testValue"))), 0, 3, None)).right.get
      articlesByAttributeNonExisting.items.size must beEqualTo(0)
    }
  }

  "The `findByTypeAndSlug` method" should {
    val date1: LocalDateTime = LocalDateTime.of(2016, 1, 15, 12, 0, 0)
    val story1 = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, date1)

    val date2: LocalDateTime = LocalDateTime.of(2016, 1, 15, 13, 0, 0)
    val story2 = CMSTestEntityUtil.createStory("title2", "title2", StoryType.Article, date2)

    val date3: LocalDateTime = LocalDateTime.of(2016, 1, 15, 14, 0, 0)
    val story3 = CMSTestEntityUtil.createStory("title3", "title3", StoryType.Article, date3)

    "should find one article when slug matches" in new WithMongo with Context {
      await(storyDao.create(story2))
      await(storyDao.create(story1))
      await(storyDao.create(story3))

      val articlesSortedFromNewest: Option[Story] = await(storyDao.findByTypeAndSlug(StoryType.Article, "title2")).right.get

      articlesSortedFromNewest.get.title must beEqualTo(story2.title)
    }

    "should find no article when slug doesn't match" in new WithMongo with Context {
      await(storyDao.create(story2))
      await(storyDao.create(story1))
      await(storyDao.create(story3))

      val articlesSortedFromNewest: Option[Story] = await(storyDao.findByTypeAndSlug(StoryType.Article, "title7")).right.get

      articlesSortedFromNewest must beNone
    }
  }

  "The `addMedia` method" should {
    "Add a Set of Media to a story" in new WithMongo with Context {

      val storyToCreate = CMSTestEntityUtil.createStory("title", "title", StoryType.Article, LocalDateTime.now())
      val createdStory = await(storyDao.create(storyToCreate)).right.get

      val media1 = Media(MediaHost.CloudinaryMedia, "http://image1", MediaType.Image)
      val media2 = Media(MediaHost.CloudinaryMedia, "http://image2", MediaType.Image)

      val result = await(storyDao.editMedia(createdStory.uuid.get, Set(media1, media2))).right.get

      result should beEqualTo(true)

      val updatedStory = await(storyDao.findByUUID(createdStory.uuid.get)).right.get.get

      updatedStory.mediaSet should haveSize(2)
    }
  }

  /**
   * The context.
   */
  trait Context extends MongoScope {
    self: WithServer =>

    implicit lazy val format = Json.format[OAuth1Info]

    lazy val storyDao = new StoryDAOImpl(reactiveMongoAPI)
  }
}
