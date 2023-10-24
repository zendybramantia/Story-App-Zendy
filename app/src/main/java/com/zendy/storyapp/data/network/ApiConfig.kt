package com.zendy.storyapp.data.network

import com.zendy.storyapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getRetrofit(): Retrofit {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        fun getApiService(): ApiService {
            return getRetrofit().create(ApiService::class.java)
        }
    }
}