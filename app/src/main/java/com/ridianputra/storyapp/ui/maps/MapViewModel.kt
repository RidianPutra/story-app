package com.ridianputra.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.ridianputra.storyapp.data.Repository

class MapViewModel(private val repository: Repository) : ViewModel() {
    fun getStoriesMap(token: String) =
        repository.getStoriesMap(token)
}