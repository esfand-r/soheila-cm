package org.soheila.cms

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.ReadJournal
import com.google.inject.name.Names
import com.google.inject.{ AbstractModule, Provides }
import net.codingwell.scalaguice.ScalaModule
import io.soheila.cms.daos.{ PublishedStoryDAOImpl, StoryDAO, StoryDAOImpl }
import io.soheila.cms.services.stories.fsm.StoryAggregateManager
import io.soheila.cms.services.metrics.influx.InfluxDbManager
import io.soheila.cms.services._
import io.soheila.cms.services.stories.StoryServiceFactory
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The base CMS module.
 */
class CMSModule extends AbstractModule with ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  def configure() = {
    bind[StoryDAO].annotatedWith(Names.named("storyDAO")).to[StoryDAOImpl].asEagerSingleton()
    bind[StoryDAO].annotatedWith(Names.named("publishedStoryDAO")).to[PublishedStoryDAOImpl].asEagerSingleton()
    bind[StoryServiceFactory].asEagerSingleton()
    bind[CMSService].to[CMSServiceImpl].asEagerSingleton()
    bindActor[StoryAggregateManager]("StoryAggregateManager")
    bindActor[InfluxDbManager]("InfluxDbManager")
  }
}
