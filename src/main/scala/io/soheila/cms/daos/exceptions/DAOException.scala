package io.soheila.cms.daos.exceptions

case class DAOException(message: String, cause: Throwable) extends RuntimeException(message, cause)
