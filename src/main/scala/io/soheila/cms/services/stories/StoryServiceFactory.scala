package io.soheila.cms.services.stories

import com.google.inject.Inject
import com.google.inject.name.Named
import io.soheila.cms.daos.StoryDAO
import io.soheila.cms.services.stories.fsm.{ Edited, Published, StoryState }

/**
 * Factory for storyService that returns a service based on on that state.
 * For performance and convenience reasons the published stories are stored in a separate document.
 */
class StoryServiceFactory @Inject() (@Named("storyDAO") val storyDAO: StoryDAO, @Named("publishedStoryDAO") val publishedStory: StoryDAO) {

  private val StoryServiceMap: Map[String, StoryService] = Map(
    Published.getClass.getSimpleName -> new StoryServiceImpl(publishedStory)
  ).withDefaultValue(new StoryServiceImpl(storyDAO))

  def getStoryService(storyState: StoryState): StoryService = {
    StoryServiceMap(storyState.getClass.getSimpleName)
  }

  def getStoryService(published: Boolean): StoryService = {
    if (published) getStoryService(Published)
    else getStoryService(Edited)
  }

  def getDefaultStoryService: StoryService = {
    getStoryService(Edited)
  }

  def getPublishedStoryService: StoryService = {
    getStoryService(Published)
  }
}
