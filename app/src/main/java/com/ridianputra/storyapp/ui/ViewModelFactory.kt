package com.ridianputra.storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ridianputra.storyapp.data.Repository
import com.ridianputra.storyapp.di.Injection
import com.ridianputra.storyapp.ui.addstory.AddStoryViewModel
import com.ridianputra.storyapp.ui.main.StoriesViewModel
import com.ridianputra.storyapp.ui.maps.MapViewModel
import com.ridianputra.storyapp.ui.signin.SignInViewModel
import com.ridianputra.storyapp.ui.signup.SignUpViewModel

class ViewModelFactory private constructor(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                return AddStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoriesViewModel::class.java) -> {
                return StoriesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                return MapViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                return SignInViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                return SignUpViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository())
            }.also { instance = it }
    }
}