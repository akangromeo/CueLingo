package com.example.cuelingo.di

import UserPreference
import android.content.Context
import com.example.cuelingo.data.remote.retrofit.ApiConfig
import com.example.cuelingo.data.repository.DictionaryRepository
import dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object InjectionDictionary {
    fun provideRepository(context: Context): DictionaryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return DictionaryRepository(apiService)
    }
}