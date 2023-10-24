package com.zendy.storyapp.ui.addNewStory

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zendy.storyapp.data.network.APIError
import com.zendy.storyapp.data.network.ApiConfig
import com.zendy.storyapp.data.network.FileUploadResponse
import com.zendy.storyapp.helper.ErrorUtils
import com.zendy.storyapp.preferences.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddNewStoryViewModel(private val pref: UserPreferences) : ViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private var _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var getFile: File? = null

    var bitmapImage: Bitmap? = null
    var uriImage: Uri? = null

    fun getToken(): LiveData<String?> {
        return pref.getToken().asLiveData()
    }

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, description: RequestBody, lat:RequestBody, lon:RequestBody) {
        _isLoading.value = true
        val client =
            ApiConfig.getApiService().uploadImage("Bearer $token", imageMultipart, description, lat, lon)
        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _message.value = responseBody.message
                        _isError.value = responseBody.error
                    }
                } else {
                    val error: APIError? = ErrorUtils.parseError(response)
                    if (error != null) {
                        _message.value = error.message()
                    }
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _message.value = t.message
            }
        })
    }

    companion object {
        private const val TAG = "AddNewStoryViewModel"
    }
}