package io.soheila.cms

import akka.actor.ActorSystem
import akka.persistence.inmemory.query.scaladsl.InMemoryReadJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.scaladsl.ReadJournal
import com.google.inject.{ AbstractModule, Provides }
import com.google.inject.name.Names
import io.soheila.cms.daos.{ PublishedStoryDAOImpl, StoryDAO, StoryDAOImpl }
import io.soheila.cms.services.stories.fsm.StoryAggregateManager
import io.soheila.cms.services.metrics.influx.InfluxDbManager
import io.soheila.cms.services._
import io.soheila.cms.services.media.ImageStorageService
import io.soheila.cms.services.media.cloudinary.CloudinaryStorageServiceImpl
import io.soheila.cms.services.stories.StoryServiceFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

/**
 * A fake Guice module for testing the application.
 */
class FakeModule(implicit val ec: ExecutionContext) extends AbstractModule with ScalaModule with AkkaGuiceSupport {

  def configure() = {
    bind[StoryDAO].annotatedWith(Names.named("storyDAO")).to[StoryDAOImpl]
    bind[StoryDAO].annotatedWith(Names.named("publishedStoryDAO")).to[PublishedStoryDAOImpl]
    bind[StoryServiceFactory].asEagerSingleton()
    bind[CMSService].to[CMSServiceImpl].asEagerSingleton()
    bind[ImageStorageService].to[CloudinaryStorageServiceImpl]
    bindActor[StoryAggregateManager]("StoryAggregateManager")
    bindActor[InfluxDbManager]("InfluxDbManager")
  }

  @Provides
  def readJournal(system: ActorSystem): ReadJournal = {
    PersistenceQuery(system).readJournalFor[InMemoryReadJournal](InMemoryReadJournal.Identifier)
  }

}
