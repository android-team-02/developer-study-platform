package com.sesac.developer_study_platform.data.source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sesac.developer_study_platform.BuildConfig
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface StudyService {

    @PUT("users/{uid}.json")
    suspend fun putUser(
        @Path("uid") uid: String,
        @Body user: StudyUser
    )

    @GET("userStudyRooms/{uid}.json")
    suspend fun getUserStudyList(
        @Path("uid") uid: String?
    ): Map<String, UserStudy>

    companion object {
        private const val BASE_URL = BuildConfig.FIREBASE_BASE_URL
        private val contentType = "application/json".toMediaType()
        private val jsonConfig = Json { ignoreUnknownKeys = true }

        fun create(): StudyService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(StudyService::class.java)
        }
    }
}