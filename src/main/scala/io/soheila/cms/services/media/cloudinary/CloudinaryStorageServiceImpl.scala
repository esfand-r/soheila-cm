package io.soheila.cms.services.media.cloudinary

import cloudinary.model.CloudinaryResourceBuilder
import com.cloudinary.Cloudinary
import com.google.inject.Inject
import io.soheila.cms.services.media.ImageStorageService
import net.ceedubs.ficus.Ficus._
import play.api.Configuration

class CloudinaryStorageServiceImpl @Inject() (val cloudinaryResourceBuilder: CloudinaryResourceBuilder, configuration: Configuration) extends ImageStorageService {

  implicit val cloudinaryClient: Cloudinary = cloudinaryResourceBuilder.cld

  private val config = configuration.underlying

  override def generateSignature(paramsToSign: Map[String, _]): String = {
    val secret = config.as[String]("cloudinary.api_secret")

    val params = paramsToSign.toList.sortBy(_._1) collect {
      case (k, v: Iterable[_]) => s"$k=${v.mkString(",")}"
      case (k, v) if v != null && v.toString != "" =>
        val value = v.toString.replace("\"", "")
        s"$k=$value"
    }

    Cloudinary.sign(params.mkString("&"), secret).map("%02X" format _).mkString
  }
}
