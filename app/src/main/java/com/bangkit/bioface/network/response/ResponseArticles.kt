package com.bangkit.bioface.network.response

import com.google.gson.annotations.SerializedName

// Model untuk daftar artikel
data class ResponseArticlesList(
	@field:SerializedName("data")
	val data: List<ArticlesItem>? = null // Untuk daftar artikel
)

// Model untuk detail artikel
data class ResponseArticleDetail(
	@field:SerializedName("data")
	val data: ArticlesItem? = null // Untuk detail artikel
)

data class ArticlesItem(
	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)