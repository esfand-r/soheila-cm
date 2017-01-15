package io.soheila.cms.services.media

trait ImageStorageService {

  def generateSignature(paramsToSign: Map[String, _]): String
}
