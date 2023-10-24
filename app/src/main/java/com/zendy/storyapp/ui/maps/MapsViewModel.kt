package com.zendy.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zendy.storyapp.data.network.APIError
import com.zendy.storyapp.data.network.ApiConfig
import com.zendy.storyapp.data.network.GetAllStoriesResponse
import com.zendy.storyapp.data.network.Story
import com.zendy.storyapp.helper.ErrorUtils
import com.zendy.storyapp.preferences.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferences) : ViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private var _listStories = MutableLiveData<List<Story>>()
    val listStories: LiveData<List<Story>> = _listStories

    fun getToken(): LiveData<String?> {
        return pref.getToken().asLiveData()
    }

    fun getAllStories(token: String) {
        val client = ApiConfig.getApiService().getAllStoriesWithLocation("Bearer $token", 1, 20)
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>,
                response: Response<GetAllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listStories.value = responseBody.listStory
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    val error: APIError? = ErrorUtils.parseError(response)
                    if (error != null) {
                        _message.value = error.message()
                    }
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                _message.value = t.message
            }
        })
    }

    companion object {
        private const val TAG = "MapsViewModel"
    }
}