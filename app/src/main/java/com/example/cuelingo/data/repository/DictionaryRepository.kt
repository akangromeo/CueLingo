package com.example.cuelingo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.cuelingo.data.remote.response.ErrorResponse
import com.example.cuelingo.data.remote.response.ListDictionaryItem
import com.example.cuelingo.data.remote.retrofit.ApiConfigDictionary
import com.example.cuelingo.data.remote.retrofit.ApiService
import com.example.cuelingo.data.result.Result
import com.google.gson.Gson
import retrofit2.HttpException

class DictionaryRepository(private val apiService: ApiService) {

    fun getAllDictionary(): LiveData<Result<List<ListDictionaryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = ApiConfigDictionary.getApiService()
            val dictionaryResponse = response.getAllDictionary()
            val dictionary = dictionaryResponse.listDictionary
            val dictionaryList = dictionary.map { it ->
                ListDictionaryItem(
                    it.id,
                    it.name,
                    it.photoUrl,
                )
            }
            if (dictionaryResponse.error != true) {
                emit(Result.Success(dictionaryList))
            } else {
                emit(Result.Error(dictionaryResponse.error.toString()))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            emit(Result.Error("Loading Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("$e"))
        }
    }

}