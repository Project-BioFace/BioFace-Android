package com.bangkit.bioface.network.api

import com.bangkit.bioface.main.adapter.HerbalItem

import com.bangkit.bioface.network.response.ArticlesItems
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.PredictionResponse
import com.bangkit.bioface.network.response.SkincareItem
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("natural") // Endpoint untuk kategori natural
    fun getNaturalHerbs(): Call<List<HerbalItem>>

    @GET("product") // Endpoint untuk kategori product
    fun getProductHerbs(): Call<List<HerbalItem>>

    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): PredictionResponse


    @GET("articles/{id}")
    suspend fun getArticleById(@Path("id") id: Int): Response<ArticlesItems>  // Menggunakan suspend untuk coroutine

    @GET("articles")  // Endpoint untuk mendapatkan artikel
    suspend fun getArticles(): Response<List<ArticlesItems>>  // Menggunakan suspend untuk coroutine

    @GET("bio/{id}")
    suspend fun getSkinCareItemById(@Path("id") id: Int): Response<SkincareItem>  // Menggunakan suspend untuk coroutine

    @GET("bio")
    suspend fun getSkinCareItems(): Response<List<SkincareItem>>  // Menggunakan suspend untuk coroutine

    @GET("biosk/{id}")
    suspend fun getDictItemById(@Path("id") id: Int): Response<DictItem>  // Menggunakan suspend untuk coroutine

    @GET("biosk")
    suspend fun getDictItems(): Response<List<DictItem>>  // Menggunakan suspend untuk coroutine
}
