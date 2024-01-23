package com.sesac.developer_study_platform.data.source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sesac.developer_study_platform.StudyApplication.Companion.sharedPref
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.data.User
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {

    @GET("user")
    suspend fun getUser(): User

    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repository>

    companion object {
        private val ACCESS_TOKEN = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        private const val BASE_URL = "https://api.github.com/"
        private val contentType = "application/json".toMediaType()
        private val jsonConfig = Json { ignoreUnknownKeys = true }

        private val client = OkHttpClient.Builder().addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("Accept", "application/vnd.github+json")
            builder.addHeader("Authorization", ACCESS_TOKEN)
            chain.proceed(builder.build())
        }.build()

        fun create(): GithubService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(GithubService::class.java)
        }
    }
}