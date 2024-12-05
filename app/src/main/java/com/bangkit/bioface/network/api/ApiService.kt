package com.bangkit.bioface.network.api

import com.bangkit.bioface.main.adapter.ImageRequest
import com.bangkit.bioface.network.response.ResponseArticleDetail
import com.bangkit.bioface.network.response.ResponseArticlesList
import com.bangkit.bioface.network.response.ResponseDictDetail
import com.bangkit.bioface.network.response.ResponseDictList
import com.bangkit.bioface.network.response.ResponseSkincareDetail
import com.bangkit.bioface.network.response.ResponseSkincareList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("predict")
    fun uploadImage(@Body request: ImageRequest): Call<ResponseBody>

    @GET("articles/{id}")
    suspend fun getArticleById(@Path("id") id: Int): Response<ResponseArticleDetail>
 // Menggunakan suspend untuk coroutine

    @GET("articles")
    suspend fun getArticles(): Response<ResponseArticlesList> // Menggunakan Response<ResponseArticles>

    @GET("bio/{id}")
    suspend fun getDictItemById(@Path("id") id: Int): Response<ResponseDictDetail>  // Menggunakan suspend untuk coroutine

    @GET("bio")
    suspend fun getDictItems(): Response<ResponseDictList> // Menggunakan suspend untuk coroutine

    @GET("biosk/{id}")
    suspend fun getSkincareItemById(@Path("id") id: Int): Response<ResponseSkincareDetail>  // Menggunakan suspend untuk coroutine

    @GET("biosk")
    suspend fun getSkincareItems(): Response<ResponseSkincareList>  // Menggunakan suspend untuk coroutine
}