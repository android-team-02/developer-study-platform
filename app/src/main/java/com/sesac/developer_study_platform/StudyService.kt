package com.sesac.developer_study_platform

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface StudyService {

    @GET("studies.json")
    suspend fun getStudyList(): List<Study>

    @GET("categories/{category}.json")
    suspend fun getStudyList(@Path("category") category: String): List<Study>

    companion object {
        private const val BASE_URL = BuildConfig.FIREBASE_BASE_URL
        private val contentType = "application/json".toMediaType()

        fun create(): StudyService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(Json.asConverterFactory(contentType))
                .build()
                .create(StudyService::class.java)
        }
    }
}