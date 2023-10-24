package com.zendy.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zendy.storyapp.data.database.StoryEntity
import com.zendy.storyapp.data.repository.StoryRepository
import com.zendy.storyapp.preferences.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreferences, private val storyRepository: StoryRepository) : ViewModel() {

    fun getToken(): LiveData<String?> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getListStories(token: String) : LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStory(token).cachedIn(viewModelScope)
}