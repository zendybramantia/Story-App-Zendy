package com.zendy.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.zendy.storyapp.data.database.StoryDatabase
import com.zendy.storyapp.data.database.StoryEntity
import com.zendy.storyapp.data.network.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getAllStory(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = QuoteRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}