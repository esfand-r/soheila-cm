package io.soheila.cms.services.metrics.influx

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import com.typesafe.config.Config

/**
 * InfluxDB configurations.
 * Created as [[http://doc.akka.io/docs/akka/current/scala/extending-akka.html AKKA extensions]].
 * and it will be used like InfluxDbConfig(context.system).
 */
class InfluxDbConfig(config: Config) extends Extension {
  val host: String = config.getString("influxDb.host")
  val port: Int = config.getInt("influxDb.port")
  val username: String = config.getString("influxDb.username")
  val password: String = config.getString("influxDb.password")
  val database: String = config.getString("influxDb.database")
}

object InfluxDbConfig extends ExtensionId[InfluxDbConfig] with ExtensionIdProvider {
  override def lookup() = InfluxDbConfig

  override def createExtension(system: ExtendedActorSystem): InfluxDbConfig =
    new InfluxDbConfig(system.settings.config)
}
