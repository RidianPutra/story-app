package com.ridianputra.storyapp.data.network.api

import com.ridianputra.storyapp.data.network.response.AddStoryResponse
import com.ridianputra.storyapp.data.network.response.SignInResponse
import com.ridianputra.storyapp.data.network.response.SignUpResponse
import com.ridianputra.storyapp.data.network.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun getUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): SignInResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoriesResponse

    @GET("stories")
    suspend fun getStoriesMap(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddStoryResponse
}