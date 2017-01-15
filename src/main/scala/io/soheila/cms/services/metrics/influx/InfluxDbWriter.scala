package io.soheila.cms.services.metrics.influx

import akka.actor.{ Actor, ActorLogging, Props }
import com.paulgoldbaum.influxdbclient.{ Database, Point }
import io.soheila.cms.services.metrics.influx.InfluxDbWriter.{ SendBatch, WritePoint }

import scala.collection.mutable
import scala.util.{ Failure, Success }

/**
 *
 */
object InfluxDbWriter {

  def props(influxDatabase: Database) = Props(new InfluxDbWriter(influxDatabase))

  case class WritePoint(point: Point)

  case object SendBatch
}

class InfluxDbWriter(influxDatabase: Database) extends Actor with ActorLogging {
  val batch = new mutable.Queue[Point]()

  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case WritePoint(point) =>
      log.debug(s"Scheduling point write [$point]")
      batch.enqueue(point)

    case SendBatch =>
      log.info("Draining data for batch request")

      val pointToSend: mutable.Seq[Point] = batch.dequeueAll((_) => true)

      if (pointToSend.nonEmpty) {

        influxDatabase.bulkWrite(pointToSend).onComplete({
          case Success(true) =>
            log.debug("Batch write succeed")

          case Success(false) =>
            log.warning("Batch write failed. Rescheduling points")
            batch ++= pointToSend

          case Failure(e) =>
            log.warning("Batch write failed. Rescheduling points", e)
            batch ++= pointToSend
        })

      }
  }
}
