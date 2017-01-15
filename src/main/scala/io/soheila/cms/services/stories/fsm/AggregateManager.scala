package io.soheila.cms.services.stories.fsm

import akka.actor.{ Actor, ActorLogging, Props }

object AggregateManager {

}

abstract class AggregateManager extends Actor with ActorLogging {
  def processCommand: Receive

  def aggregateProps(): Props

  def receive = processCommand
}
