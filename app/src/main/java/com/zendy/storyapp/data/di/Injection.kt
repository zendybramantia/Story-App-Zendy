package com.zendy.storyapp.data.di

import android.content.Context
import com.zendy.storyapp.data.database.StoryDatabase
import com.zendy.storyapp.data.network.ApiConfig
import com.zendy.storyapp.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database,apiService)
    }
}