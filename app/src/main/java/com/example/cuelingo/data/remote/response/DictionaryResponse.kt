package com.example.cuelingo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DictionaryResponse(

    @field:SerializedName("listDictionary")
    val listDictionary: List<ListDictionaryItem> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ListDictionaryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: String? = null
)
