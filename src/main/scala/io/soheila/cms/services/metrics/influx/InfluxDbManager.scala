package io.soheila.cms.services.metrics.influx

import java.util.concurrent.TimeUnit

import akka.actor.{ Actor, ActorLogging }
import com.paulgoldbaum.influxdbclient.{ InfluxDB, Point }
import io.soheila.cms.services.metrics.influx.InfluxDbManager.{ Shutdown, Write }
import io.soheila.cms.services.metrics.influx.InfluxDbWriter.{ SendBatch, WritePoint }

import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Actor to manage writing time series into Influx.
 */
object InfluxDbManager {
  case class Write(tags: Map[String, String])

  case object Shutdown
}

class InfluxDbManager extends Actor with ActorLogging {
  val influxDbSettings = InfluxDbConfig(context.system)

  override def receive: Receive = {
    case Write(tags) =>
      // For now we will only add the count field. We will use Influx for queries such as "Number of page visit for user A in August".
      val point = Point("cms-census")
        .addField("count", 1)

      val taggedPoint = tags.foldLeft(point)((point, entry) => point.addTag(entry._1, entry._2))

      influxDbWriter ! WritePoint(taggedPoint)

    case Shutdown =>
      database.close()
      influxDb.close()
  }

  val influxDb = InfluxDB.connect(
    influxDbSettings.host,
    influxDbSettings.port,
    influxDbSettings.username,
    influxDbSettings.password
  )

  val database = influxDb.selectDatabase(influxDbSettings.database)
  database.exists() andThen {
    case Failure(e) => s"Connection to Influx failed Failed $e"
    case Success(true) =>
      log debug s"Influx Database exists."
    case Success(false) =>
      log info s"Influx Database does not exist, creating new database"
      database.create() andThen {
        case Failure(e) => log warning s"Failed $e"
        case Success(q) => log info s"Database created $q"
      }
  }

  val duration = Duration.create(300, TimeUnit.MILLISECONDS)

  val influxDbWriter = context.actorOf(InfluxDbWriter.props(database))

  context.system.scheduler.schedule(duration, duration, influxDbWriter, SendBatch)
}
