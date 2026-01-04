package com.rendizar.uts_api.api

import com.rendizar.uts_api.api.services.SongService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    // 1. Create the Retrofit instance (lazy ensures it's created only once)
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Expose the SongService directly (lazy ensures it's created only once)
    val songService: SongService by lazy {
        retrofit.create(SongService::class.java)
    }
}
