package io.soheila.cms.daos

import com.google.inject.Inject
import io.soheila.commons.crud.MongoCRUDDAO
import io.soheila.commons.entities.Page
import grizzled.slf4j.Logger
import io.soheila.cms.daos.exceptions.DAOException
import io.soheila.cms.entities.{ Media, Story }
import io.soheila.cms.types.StoryType
import play.api.libs.json.{ JsArray, JsObject, Json }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.indexes.{ Index, IndexType }
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }

class StoryDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi)(implicit override val ec: ExecutionContext) extends MongoCRUDDAO[Story, String] with StoryDAO {
  private val logger = Logger[this.type]

  override def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("stories"))

  override def findLatestByType(storyType: StoryType.Value, limit: Int): Future[Either[DAOException, List[Story]]] = {
    collection.flatMap(_.find(Json.obj("storyType" -> storyType))
      .sort(Json.obj("publishedOn" -> -1))
      .cursor[Story]()
      .collect[List](limit, Cursor.FailOnError((_: List[Story], err) =>
        logger.error(
          s"Fail during collection results in StoryDAO.findLatestByType.",
          err
        ))))
      .map(le => Right(le))
      .recover {
        case err =>
          logger.error(err.getMessage, err)
          Left(DAOException(err.getMessage, err))
      }
  }

  override def findByTags(tags: Seq[String], page: Int, limit: Int, sortFilter: Option[(String, Int)]): Future[Either[DAOException, Page[Story]]] = {
    val query = Json.obj("tags" -> Json.obj("$in" -> tags))
    find(query, page, limit, sortFilter).map {
      case Left(err) => Left(DAOException(err.getMessage, err))
      case Right(res) => Right(res)
    }
  }

  override def findByAttributes(attributes: Map[String, Seq[String]], page: Int, limit: Int, sortFilter: Option[(String, Int)]): Future[Either[DAOException, Page[Story]]] = {
    val attributeArray: Seq[JsObject] = for (
      (key, value) <- attributes.toSeq
    ) yield Json.obj("$elemMatch" -> Json.obj("key" -> key, "value" -> value))

    val queryArray: JsArray = attributeArray.foldLeft(Json.arr()) {
      (jsonArray: JsArray, query: JsObject) => jsonArray.append(query)
    }

    val query = Json.obj("attributes" -> Json.obj("$all" -> queryArray))
    find(query, page, limit, sortFilter).map {
      case Left(err) => Left(DAOException(err.getMessage, err))
      case Right(res) => Right(res)
    }
  }

  override def findByTypeAndSlug(storyType: StoryType.Value, slug: String): Future[Either[DAOException, Option[Story]]] = {
    findOne(Json.obj("storyType" -> storyType, "slug" -> slug)).map {
      case Left(err) => Left(DAOException(err.getMessage, err))
      case Right(res) => Right(res)
    }
  }

  override def findByUUID(uuid: String): Future[Either[DAOException, Option[Story]]] = {
    findOne(Json.obj("uuid" -> uuid)).map {
      case Left(err) => Left(DAOException(err.getMessage, err))
      case Right(res) => Right(res)
    }
  }

  override def updateTitle(uuid: String, title: String, currentNonce: String): Future[Either[DAOException, Option[Story]]] = {
    collection.flatMap(col => {
      val updateOp = col.updateModifier(Json.obj("$set" -> Json.obj("title" -> title, "nonce" -> BSONObjectID.generate().stringify)), fetchNewObject = true)

      col.findAndModify(Json.obj("uuid" -> uuid, "nonce" -> currentNonce), updateOp) map {
        case le if le.value.isDefined => Right(le.result[Story])
        case le if le.lastError.isDefined => Left(DAOException(le.lastError.get.err.getOrElse(UnknownErrorMessage), null))
        case _ => Left(DAOException(UnknownErrorMessage, null))
      }
    })
  }

  override def findAndUpdateLatest(story: Story, upsert: Boolean): Future[Either[DAOException, Option[Story]]] = {
    findAndUpdateByCriteria(Json.obj("uuid" -> story.uuid.get, "updatedOn" -> Json.obj("$lt" -> story.updatedOn.get)), story, upsert)
      .map {
        case Left(err) => Left(DAOException(err.getMessage, err))
        case Right(res) => Right(res)
      }
  }

  override def editMedia(uuid: String, medias: Set[Media]): Future[Either[DAOException, Boolean]] = {
    collection.flatMap(col => {
      val addMedia = Json.obj("$addToSet" -> Json.obj("mediaSet" -> Json.obj("$each" -> Json.toJson(medias))))
      col.update(Json.obj("uuid" -> uuid), addMedia) map {
        case le if le.ok => Right(true)
        case le if Option(le.errmsg).isDefined => Left(DAOException(le.errmsg.get, null))
        case _ => Left(DAOException(UnknownErrorMessage, null))
      }
    })
  }

  override def indexSet: Set[Index] = {
    Set(
      Index(List("uuid" -> IndexType.Descending, "language" -> IndexType.Ascending), unique = true),
      Index(List("title" -> IndexType.Text, "title" -> IndexType.Text, "content.text" -> IndexType.Text)),
      Index(List("storyType" -> IndexType.Ascending, "slug" -> IndexType.Ascending), unique = true),
      Index(List("tags" -> IndexType.Ascending)),
      Index(List("storyType" -> IndexType.Ascending, "publishedOn" -> IndexType.Descending)),
      Index(List("attributes.key" -> IndexType.Ascending, "attributes.value" -> IndexType.Ascending))
    )
  }
}
