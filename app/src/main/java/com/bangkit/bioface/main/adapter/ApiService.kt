package com.bangkit.bioface.main.adapter

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("natural") // Endpoint untuk kategori natural
    fun getNaturalHerbs(): Call<List<HerbalItem>>

    @GET("product") // Endpoint untuk kategori product
    fun getProductHerbs(): Call<List<HerbalItem>>

    @POST("predict")
    fun uploadImage(@Body request: ImageRequest): Call<ResponseBody>
}