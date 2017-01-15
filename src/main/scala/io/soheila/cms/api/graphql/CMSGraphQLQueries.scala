package io.soheila.cms.api.graphql

import sangria.marshalling._
import sangria.schema.{ Field, _ }
import sangria.schema.Action._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import io.soheila.cms.api.graphql.CMSGraphQLTypes._
import io.soheila.cms.api.graphql.CMSGraphQLInputTypes._
import io.soheila.cms.entities.CMSUserRole
import io.soheila.cms.vos.StoryUpdateVO

object CMSGraphQLQueries {
  val UUIDArg = Argument("uuid", StringType, description = "The UUID of the story")
  val UserUUIDArg = Argument("userUUID", StringType, description = "The UUID of a user")
  val UserNameArg = Argument("userName", StringType, description = "The name of a user")
  val StoryTitleArg = Argument("title", StringType, description = "Article title")
  val StoryTypeArg = Argument("storyType", StoryTypeEnum, description = "Type of the story")
  val StorySlugArg = Argument("slug", OptionInputType(StringType), description = "slug of the story")
  val StoryHeadlineArg = Argument("headline", StringType, description = "headline of the story")
  val ContentArg = Argument("content", OptionInputType(ContentInputType), "Content of a Story")
  val UserReferenceArg = Argument("userReference", UserReferenceInputType, "User Reference")
  val MainMediaArg = Argument("mainMedia", OptionInputType(MediaInputType), "Main media of a Story")
  val MediaSetArg = Argument("mediaSet", ListInputType(MediaInputType), "Media set of a Story")
  val AuthorsArg = Argument("authors", OptionInputType(ListInputType(UserReferenceInputType)), "Authors of a Story")
  val TagsArg = Argument("tags", OptionInputType(ListInputType(StringType)))
  val AttributeArg = Argument("attributes", OptionInputType(ListInputType(AttributeInputType)))
  val limitArgument = Argument(
    name = "limit",
    argumentType = OptionInputType(IntType),
    defaultValue = 20, // todo: externalize
    description = "Maximum number of results to include in response"
  )
  val startArgument = Argument(
    name = "start",
    argumentType = OptionInputType(StringType),
    description = "Cursor to start pagination at"
  )

  val paginationArguments = List(limitArgument, startArgument)

  // todo: cleanup exceptions in the enxt round.
  val Query = ObjectType(
    "Query", fields[CMSSecureContext, Unit](
      Field("storyByUuid", OptionType(StorySchemaType), arguments = UUIDArg :: Nil, resolve = (ctx) => ctx.ctx.cMSService.readStoryByUUID(ctx.arg(UUIDArg))),
      Field("storyByTypeAndSlug", OptionType(StorySchemaType), arguments = StoryTypeArg :: StorySlugArg :: Nil, resolve = (ctx) => ctx.ctx.storyServiceFactory.getDefaultStoryService.findByTypeAndSlug(ctx.arg(StoryTypeArg), ctx.arg(StorySlugArg).getOrElse(throw new IllegalArgumentException("Slug must be provided"))))
    )
  )

  val Mutation = ObjectType(
    "Mutation", fields[CMSSecureContext, Unit](
      Field("createInitialStory", OptionType(StorySchemaType), arguments = StoryTypeArg :: Nil,
        resolve = (ctx) => ctx.ctx.authorised(CMSUserRole.CMS_Admin) {
        _ => ctx.ctx.cMSService.initializeStory(ctx.arg(StoryTypeArg), ctx.ctx.user.get)
      }),
      Field("updateStory", OptionType(StorySchemaType),
        arguments = UUIDArg :: StoryTitleArg :: StorySlugArg :: StoryTypeArg :: StoryHeadlineArg ::
        AuthorsArg :: TagsArg :: MainMediaArg :: ContentArg :: AttributeArg :: Nil,
        resolve = (ctx) => ctx.ctx.authorised(CMSUserRole.CMS_Admin) {
        _ =>
          val story: StoryUpdateVO = StoryUpdateVO.validated(ctx.arg(UUIDArg), ctx.arg(StoryTypeArg), ctx.arg(StoryTitleArg), ctx.arg(StorySlugArg), ctx.arg(StoryHeadlineArg),
            ctx.arg(AuthorsArg), ctx.arg(TagsArg), ctx.arg(MainMediaArg), ctx.arg(ContentArg), ctx.arg(AttributeArg), ctx.ctx.user.get).toEither.right.get

          // todo: fix toEither right above and handle errors properly.
          ctx.ctx.cMSService.updateStory(story)
      }),
      Field("editMedia", OptionType(BooleanType), arguments = UUIDArg :: MediaSetArg :: Nil,
        resolve = (ctx) => ctx.ctx.authorised(CMSUserRole.CMS_Admin) {
        _ => ctx.ctx.cMSService.editStoryMedia(ctx.arg(UUIDArg), ctx.arg(MediaSetArg).toSet)
      })
    )
  )

  val StorySchema = Schema(Query, Some(Mutation))
}
