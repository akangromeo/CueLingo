package com.example.cuelingo.data.local.preferences

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)