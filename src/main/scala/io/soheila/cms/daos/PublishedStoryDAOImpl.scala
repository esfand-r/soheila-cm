package io.soheila.cms.daos

import javax.inject.Inject

import grizzled.slf4j.Logger
import io.soheila.cms.daos.exceptions.DAOException
import io.soheila.cms.entities.Story
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }

class PublishedStoryDAOImpl @Inject() (override val reactiveMongoApi: ReactiveMongoApi)(implicit override val ec: ExecutionContext) extends StoryDAOImpl(reactiveMongoApi) {
  private val logger = Logger[this.type]

  override def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("published_stories"))

  override def findAndUpdateLatest(story: Story, upsert: Boolean): Future[Either[DAOException, Option[Story]]] = {
    findAndUpdateByCriteria(Json.obj("uuid" -> story.uuid.get), story, upsert)
      .map {
        case Left(err) => Left(DAOException(err.getMessage, err))
        case Right(res) => Right(res)
      }
  }
}
