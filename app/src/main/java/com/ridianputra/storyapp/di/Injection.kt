package com.ridianputra.storyapp.di

import com.ridianputra.storyapp.data.Repository
import com.ridianputra.storyapp.data.network.api.ApiConfig

object Injection {
    fun provideRepository(): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}