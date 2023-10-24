package com.zendy.storyapp.ui.splashScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zendy.storyapp.preferences.UserPreferences

class SplashScreenViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getToken(): LiveData<String?> {
        return pref.getToken().asLiveData()
    }
}