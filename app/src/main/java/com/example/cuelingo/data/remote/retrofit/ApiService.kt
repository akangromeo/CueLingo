package com.example.cuelingo.data.remote.retrofit

import com.example.cuelingo.data.remote.response.DictionaryResponse
import com.example.cuelingo.data.remote.response.LoginResponse
import com.example.cuelingo.data.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("dictionary.json")
    suspend fun getAllDictionary():DictionaryResponse

//    @GET("stories/{id}")
//    suspend fun getStoryDetail(
//        @Path("id") id: String
//    ): DetailStoryResponse
}