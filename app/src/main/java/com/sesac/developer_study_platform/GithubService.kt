package com.sesac.developer_study_platform

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface GithubService {

    @Headers("Accept: application/vnd.github+json")
    @GET("user")
    suspend fun getUser(
        @Header("Authorization") authorization : String
    ) : User

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            val jsonConfig = Json { ignoreUnknownKeys = true }
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(GithubService::class.java)
        }
    }
}