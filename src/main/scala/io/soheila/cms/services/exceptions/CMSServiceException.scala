package io.soheila.cms.services.exceptions

case class CMSServiceException(message: String, cause: Throwable) extends RuntimeException(message, cause)
