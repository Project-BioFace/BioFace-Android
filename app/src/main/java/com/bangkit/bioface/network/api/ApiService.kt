package com.bangkit.bioface.network.api

import com.bangkit.bioface.network.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("prediction")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): PredictionResponse

    @GET("history")
    suspend fun getHistory(@Header("Authorization") token: String): HistoryResponse

    @GET("history/{id}")
    suspend fun getHistoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): PredictionHistoryResponse

    @DELETE("history/{id}")
    suspend fun deletePredictionHistory(
        @Header("Authorization") token: String,
        @Path("id") predictionId: Int
    ): BaseResponse

    @DELETE("history/delete-all")
    suspend fun deleteAllHistory(
        @Header("Authorization") token: String
    ): BaseResponse
}
