package com.zendy.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zendy.storyapp.data.network.APIError
import com.zendy.storyapp.data.network.ApiConfig
import com.zendy.storyapp.data.network.LoginResponse
import com.zendy.storyapp.helper.ErrorUtils
import com.zendy.storyapp.preferences.UserPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val pref: UserPreferences) : ViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private var _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        saveToken(responseBody.loginResult.token)
                        _isError.value = responseBody.error
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    val error: APIError? = ErrorUtils.parseError(response)
                    if (error != null) {
                        _message.value = error.message()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _message.value = t.message
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}