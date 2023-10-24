package com.zendy.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zendy.storyapp.data.network.APIError
import com.zendy.storyapp.data.network.ApiConfig
import com.zendy.storyapp.data.network.RegisterResponse
import com.zendy.storyapp.helper.ErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(email: String, name: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isError.value = responseBody.error
                        _message.value = responseBody.message
                    }
                } else {
                    val error: APIError? = ErrorUtils.parseError(response)
                    if (error != null) {
                        _isError.value = true
                        _message.value = error.message()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isError.value = true
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _message.value = t.message
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}