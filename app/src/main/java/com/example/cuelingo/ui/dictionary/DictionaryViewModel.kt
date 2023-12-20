package com.example.cuelingo.ui.dictionary

import androidx.lifecycle.ViewModel
import com.example.cuelingo.data.repository.DictionaryRepository

class DictionaryViewModel(private val repository: DictionaryRepository): ViewModel() {

    fun getAllDictionary() = repository.getAllDictionary()



}