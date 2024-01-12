package com.sesac.developer_study_platform

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface StudyService {

    @PUT("users/{uid}.json")
    suspend fun putUser(
        @Path("uid") uid: String,
        @Body user: StudyUser
    )

    companion object {
        private const val BASE_URL = BuildConfig.DB_BASE_URL

        fun create(): StudyService {
            val jsonConfig = Json { ignoreUnknownKeys = true }
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(StudyService::class.java)
        }
    }
}