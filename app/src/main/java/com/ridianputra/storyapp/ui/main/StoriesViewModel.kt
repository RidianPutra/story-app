package com.ridianputra.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ridianputra.storyapp.data.Repository
import com.ridianputra.storyapp.data.network.response.ListStoryItem

class StoriesViewModel(private val repository: Repository) : ViewModel() {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        repository.getStories(token).cachedIn(viewModelScope)
}