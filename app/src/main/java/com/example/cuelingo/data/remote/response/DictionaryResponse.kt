package com.example.cuelingo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DictionaryResponse(

	@field:SerializedName("listDictionary")
	val listStory: List<DictionaryItem> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean? = null

)

data class DictionaryItem(
	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

)
