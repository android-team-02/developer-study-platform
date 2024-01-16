package com.sesac.developer_study_platform

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface StudyService {

    @GET("userStudyRooms/{uid}.json") //uid.json
    suspend fun getStudyList(
        @Path("uid") uid: String,
    ): Map<String, UserStudyRoom>

    @GET("studies/{sid}.json") //sid.json
    suspend fun getDetail(
        @Path("sid") sid: String,
    ): Study

    @GET("users/{uid}.json")
    suspend fun getUserById(
        @Path("uid") uid: String,
    ): Users

    companion object {
        private const val BASE_URL = BuildConfig.FIREBASE_URL
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