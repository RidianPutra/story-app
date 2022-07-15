package com.ridianputra.storyapp.data.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ridianputra.storyapp.data.network.response.LoginResult
import kotlinx.coroutines.launch

class UserViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getUserSession(): LiveData<LoginResult> = pref.getUserSession().asLiveData()

    fun saveUserSession(name: String, userId: String, token: String) {
        viewModelScope.launch {
            pref.saveUserSession(name, userId, token)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}