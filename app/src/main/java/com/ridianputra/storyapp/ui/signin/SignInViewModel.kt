package com.ridianputra.storyapp.ui.signin

import androidx.lifecycle.ViewModel
import com.ridianputra.storyapp.data.Repository

class SignInViewModel(private val repository: Repository) : ViewModel() {
    fun getUser(email: String, pass: String) =
        repository.getUser(email, pass)
}