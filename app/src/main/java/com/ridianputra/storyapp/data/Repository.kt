package com.ridianputra.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ridianputra.storyapp.data.network.api.ApiService
import com.ridianputra.storyapp.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val apiService: ApiService) {

    fun createUser(name: String, email: String, pass: String): LiveData<Result<SignUpResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.createUser(name, email, pass)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.e("Repository", "createUser: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getUser(email: String, pass: String): LiveData<Result<LoginResult>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getUser(email, pass)
                val loginResult = response.loginResult
                emit(Result.Success(loginResult))
            } catch (e: Exception) {
                Log.e("Repository", "getUser: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun uploadStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory("Bearer $token", imageMultipart, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("Repository", "uploadStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesMap(token: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesMap("Bearer $token")
            val listStoryItem = response.listStory
            emit(Result.Success(listStoryItem))
        } catch (e: Exception) {
            Log.e("Repository", "getStoriesMap: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, "Bearer $token")
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService)
            }.also { instance = it }
    }
}