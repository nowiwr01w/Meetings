package com.nowiwr01p.data.stories

import com.google.firebase.database.ktx.getValue
import com.nowiwr01p.core.extenstion.createEventListener
import com.nowiwr01p.domain.AppDispatchers
import com.nowiwr01p.domain.firebase.FirebaseReferencesRepository
import com.nowiwr01p.domain.meetings.main.data.Story
import com.nowiwr01p.domain.stories.client.StoriesClient
import com.nowiwr01p.domain.user.client.UserClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StoriesClientImpl(
    private val userClient: UserClient,
    private val references: FirebaseReferencesRepository,
    private val dispatchers: AppDispatchers
): StoriesClient {

    private val storiesFlow: MutableStateFlow<List<Story>> = MutableStateFlow(listOf())

    /**
     * GET STORIES
     */
    override suspend fun getStories() = storiesFlow

    override suspend fun subscribeStories(): Unit = withContext(dispatchers.io) {
        val listener = createEventListener<Map<String, Story>> { map ->
            CoroutineScope(dispatchers.io).launch {
                storiesFlow.emit(map.values.toList())
            }
        }
        references.getStoriesReference().addValueEventListener(listener)
    }

    /**
     * SET STORY VIEWED
     */
    override suspend fun setStoryViewed(storyId: String) = withContext(dispatchers.io) {
        val userId = userClient.getUserFlow().value.id
        val updatedStory = references.getStoriesReference().child(storyId).get().await()
            .getValue<Story>()!!
            .updateStory(userId)
        references.getStoriesReference().child(storyId).setValue(updatedStory).await()
        updatedStory
    }

    private fun Story.updateStory(userId: String): Story {
        val updatedViewers = viewers.toMutableList().apply { add(userId) }
        return copy(viewers = updatedViewers)
    }
}