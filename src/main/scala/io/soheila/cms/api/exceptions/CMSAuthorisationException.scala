package io.soheila.cms.api.exceptions

case class CMSAuthorisationException(message: String) extends RuntimeException(message)
