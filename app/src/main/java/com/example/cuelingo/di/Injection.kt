package com.example.cuelingo.di

import UserPreference
import android.content.Context
import com.example.cuelingo.data.remote.retrofit.ApiConfig
import com.example.cuelingo.data.repository.UserRepository
import dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}