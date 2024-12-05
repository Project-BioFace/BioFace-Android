package com.bangkit.bioface.network.response

import com.google.gson.annotations.SerializedName

// Model untuk daftar artikel
data class ResponseSkincareList(
	@field:SerializedName("data")
	val data: List<SkincareItem>? = null
)

// Model untuk detail artikel
data class ResponseSkincareDetail(
	@field:SerializedName("data")
	val data: SkincareItem? = null
)

data class SkincareItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("link_to_buy")
	val linkToBuy: String? = null,

	@field:SerializedName("benefit")
	val benefit: String? = null
)
