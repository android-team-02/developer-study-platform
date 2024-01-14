package com.sesac.developer_study_platform

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sesac.developer_study_platform.StudyApplication.Companion.sharedPref
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers

interface GithubService {

    @Headers("Accept: application/vnd.github+json")
    @GET("user")
    suspend fun getUser(): User

    companion object {

        private val ACCESS_TOKEN = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        private const val BASE_URL = "https://api.github.com/"

        private val client = OkHttpClient.Builder().addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("Authorization", ACCESS_TOKEN)
            chain.proceed(builder.build())
        }.build()

        fun create(): GithubService {
            val jsonConfig = Json { ignoreUnknownKeys = true }
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(GithubService::class.java)
        }
    }
}