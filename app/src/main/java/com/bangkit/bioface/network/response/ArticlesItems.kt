package com.bangkit.bioface.network.response

data class ArticlesItems(
    val id: Int,
    val title: String,
    val image: String?,    // Nullable string untuk image, karena bisa null
    val source: String,
    val createdAt: String,
    val content: String
)
