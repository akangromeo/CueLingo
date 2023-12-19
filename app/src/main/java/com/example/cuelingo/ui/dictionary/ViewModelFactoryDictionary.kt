package com.example.cuelingo.ui.dictionary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cuelingo.data.repository.DictionaryRepository
import com.example.cuelingo.di.InjectionDictionary

class ViewModelFactoryDictionary(private val repository: DictionaryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(DictionaryViewModel::class.java) -> {
                DictionaryViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactoryDictionary? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactoryDictionary {
            if (INSTANCE == null) {
                synchronized(ViewModelFactoryDictionary::class.java) {
                    INSTANCE = ViewModelFactoryDictionary(InjectionDictionary.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactoryDictionary
        }
    }
}