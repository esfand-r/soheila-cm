package io.soheila.cms.api.graphql

import io.soheila.commons.entities.Attribute
import io.soheila.cms.api.graphql.CMSGraphQLTypes.{ ContentTypeEnum, MediaHostTypeEnum, MediaTypeEnum }
import io.soheila.cms.entities._
import sangria.marshalling.{ CoercedScalaResultMarshaller, FromInput }
import sangria.schema.{ InputField, InputObjectType, ListInputType, StringType }
import sangria.marshalling._

object CMSGraphQLInputTypes {
  val ContentInputType = InputObjectType[Content]("content", "Content",
    List(
      InputField("text", StringType),
      InputField("contentType", ContentTypeEnum)
    ))

  implicit val contentFromInput = new FromInput[Content] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      Content(
        text = ad("text").asInstanceOf[String],
        rawText = ad("text").asInstanceOf[String],
        contentType = ad("contentType").asInstanceOf[ContentType.Value]
      )
    }
  }

  val UserReferenceInputType = InputObjectType[UserReference]("userReference", "User Reference",
    List(
      InputField("uuid", StringType),
      InputField("name", StringType)
    ))

  implicit val userReferenceFromInput = new FromInput[UserReference] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      UserReference(
        uuid = ad("uuid").asInstanceOf[String],
        name = ad("name").asInstanceOf[String]
      )
    }
  }

  val MediaInputType = InputObjectType[Media]("media", "Media",
    List(
      InputField("mediaHost", MediaHostTypeEnum),
      InputField("url", StringType),
      InputField("mediaType", MediaTypeEnum)
    ))

  implicit val mediaFromInput = new FromInput[Media] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      Media(
        mediaHost = ad("mediaHost").asInstanceOf[MediaHost.Value],
        url = ad("url").asInstanceOf[String],
        mediaType = ad("mediaType").asInstanceOf[MediaType.Value]
      )
    }
  }

  val AttributeInputType = InputObjectType[Attribute]("attribute", "Attribute",
    List(
      InputField("key", StringType),
      InputField("value", ListInputType(StringType))
    ))

  implicit val attributeFromInput = new FromInput[Attribute] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      Attribute(
        key = ad("key").asInstanceOf[String],
        value = ad("value").asInstanceOf[Seq[String]]
      )
    }
  }
}
