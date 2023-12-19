package com.example.cuelingo.ui.register

import androidx.lifecycle.ViewModel
import com.example.cuelingo.data.repository.UserRepository

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}