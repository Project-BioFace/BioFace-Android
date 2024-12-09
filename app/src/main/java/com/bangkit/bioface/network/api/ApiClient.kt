package com.bangkit.bioface.network.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL_1 = "https://bioface-model-api-542945642521.asia-southeast2.run.app"
    private const val BASE_URL_2 = "https://bioface.et.r.appspot.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun apiService1(): ApiService {
        return getRetrofit(BASE_URL_1).create(ApiService::class.java)
    }

    fun apiService2(): ApiServiceSecond {
        return getRetrofit(BASE_URL_2).create(ApiServiceSecond::class.java)
    }
}
