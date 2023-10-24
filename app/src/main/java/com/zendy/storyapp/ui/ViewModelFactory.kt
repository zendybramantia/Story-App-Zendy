package com.zendy.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zendy.storyapp.data.di.Injection
import com.zendy.storyapp.preferences.UserPreferences
import com.zendy.storyapp.ui.addNewStory.AddNewStoryViewModel
import com.zendy.storyapp.ui.login.LoginViewModel
import com.zendy.storyapp.ui.main.MainViewModel
import com.zendy.storyapp.ui.maps.MapsViewModel
import com.zendy.storyapp.ui.splashScreen.SplashScreenViewModel

class ViewModelFactory(private val pref: UserPreferences, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddNewStoryViewModel::class.java) -> {
                AddNewStoryViewModel(pref) as T
            }

            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(pref) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}