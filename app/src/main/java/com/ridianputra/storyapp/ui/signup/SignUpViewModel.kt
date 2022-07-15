package com.ridianputra.storyapp.ui.signup

import androidx.lifecycle.ViewModel
import com.ridianputra.storyapp.data.Repository

class SignUpViewModel(private val repository: Repository) : ViewModel() {
    fun createUser(name: String, email: String, pass: String) =
        repository.createUser(name, email, pass)
}