package io.soheila.cms.api.exceptions

case class CMSAuthenticationException(message: String) extends RuntimeException(message)
