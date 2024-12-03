package com.bangkit.bioface.network.response

import com.google.gson.annotations.SerializedName

data class ResponseDict(

	@field:SerializedName("data")
	val data: List<DictItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DictItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("how_to_use")
	val howToUse: String? = null,

	@field:SerializedName("benefit")
	val benefit: String? = null
)
