package com.ridianputra.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import com.ridianputra.storyapp.data.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: Repository) : ViewModel() {
    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, description: RequestBody) =
        repository.uploadStory(token, imageMultipart, description)
}