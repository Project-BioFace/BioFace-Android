package com.bangkit.bioface.network.api

import com.bangkit.bioface.main.adapter.HerbalItem
import com.bangkit.bioface.main.adapter.ImageRequest
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.ResponseArticleDetail
import com.bangkit.bioface.network.response.ResponseArticlesList
import com.bangkit.bioface.network.response.SkincareItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("natural") // Endpoint untuk kategori natural
    fun getNaturalHerbs(): Call<List<HerbalItem>>

    @GET("product") // Endpoint untuk kategori product
    fun getProductHerbs(): Call<List<HerbalItem>>

    @POST("predict")
    fun uploadImage(@Body request: ImageRequest): Call<ResponseBody>

    @GET("articles/{id}")
    suspend fun getArticleById(@Path("id") id: Int): Response<ResponseArticleDetail>
 // Menggunakan suspend untuk coroutine

    @GET("articles")
    suspend fun getArticles(): Response<ResponseArticlesList> // Menggunakan Response<ResponseArticles>

    @GET("bio/{id}")
    suspend fun getSkinCareItemById(@Path("id") id: Int): Response<SkincareItem>  // Menggunakan suspend untuk coroutine

    @GET("bio")
    suspend fun getSkinCareItems(): Response<List<SkincareItem>>  // Menggunakan suspend untuk coroutine

    @GET("biosk/{id}")
    suspend fun getDictItemById(@Path("id") id: Int): Response<DictItem>  // Menggunakan suspend untuk coroutine

    @GET("biosk")
    suspend fun getDictItems(): Response<List<DictItem>>  // Menggunakan suspend untuk coroutine
}