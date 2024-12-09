package com.bangkit.bioface.network.api

import com.bangkit.bioface.network.response.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceSecond {
    @GET("articles/{id}")
    suspend fun getArticleById(@Path("id") id: Int): Response<ResponseArticleDetail>

    @GET("articles")
    suspend fun getArticles(): Response<ResponseArticlesList>

    @GET("bio/{id}")
    suspend fun getDictItemById(@Path("id") id: Int): Response<ResponseDictDetail>

    @GET("bio")
    suspend fun getDictItems(): Response<ResponseDictList>

    @GET("biosk/{id}")
    suspend fun getSkincareItemById(@Path("id") id: Int): Response<ResponseSkincareDetail>

    @GET("biosk")
    suspend fun getSkincareItems(): Response<ResponseSkincareList>
}
