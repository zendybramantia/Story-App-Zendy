package com.zendy.storyapp.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("/v1/stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesResponse

    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): Call<FileUploadResponse>

    @GET("/v1/stories")
    fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int,
        @Query("size") size: Int
    ): Call<GetAllStoriesResponse>
}