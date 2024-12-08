package com.bangkit.bioface.network.api

import com.bangkit.bioface.main.adapter.HerbalItem

import com.bangkit.bioface.network.response.ArticlesItems
import com.bangkit.bioface.main.adapter.ImageRequest
import com.bangkit.bioface.network.response.ApiResponse
import com.bangkit.bioface.network.response.BaseResponse
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.HistoryResponse
import com.bangkit.bioface.network.response.PredictionHistory
import com.bangkit.bioface.network.response.PredictionHistoryResponse
import com.bangkit.bioface.network.response.PredictionResponse
import com.bangkit.bioface.network.response.ResponseArticleDetail
import com.bangkit.bioface.network.response.ResponseArticlesList
import com.bangkit.bioface.network.response.SkincareItem
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
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
    @POST("prediction")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): PredictionResponse

    // Mendapatkan data history
    @GET("history")
    suspend fun getHistory(@Header("Authorization") token: String): HistoryResponse

    // Mendapatkan detail prediksi berdasarkan ID
    @GET("history/{id}")
    suspend fun getHistoryDetail(@Header("Authorization") token: String, @Path("id") id: Int): PredictionHistoryResponse

    // Menghapus history berdasarkan ID
    @DELETE("history/{id}")
    suspend fun deletePredictionHistory(
        @Header("Authorization") token: String,
        @Path("id") predictionId: Int
    ): BaseResponse

    // Menghapus semua history
    @DELETE("history/delete-all")
    suspend fun deleteAllHistory(
        @Header("Authorization") token: String
    ): BaseResponse


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
