package io.soheila.cms.vos

import cloudinary.model.CloudinaryResource
import com.cloudinary.Implicits._
import com.cloudinary.{ Cloudinary, Transformation }
import org.joda.time.DateTime

case class PhotoDetails(title: String)

case class Photo(id: Long, title: String, image: CloudinaryResource, bytes: Int, createdAt: DateTime) {
  def url(implicit cld: Cloudinary) = image.url()
  def thumbnailUrl(implicit cld: Cloudinary) = image.url(Transformation().w_(150).h_(150).c_("fit").quality(80))
}
