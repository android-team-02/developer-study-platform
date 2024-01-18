package com.sesac.developer_study_platform.data.source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sesac.developer_study_platform.BuildConfig
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.UserChatRoom
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("studies.json")
    suspend fun getStudyList(
        @Query("equalTo") category: String,
        @Query("orderBy") orderBy: String = "\"category\""
    ): Map<String, Study>

    @GET("studies/{sid}.json")
    suspend fun getDetail(
        @Path("sid") sid: String,
    ): Study

    @GET("users/{uid}.json")
    suspend fun getUserById(
        @Path("uid") uid: String,
    ): StudyUser

    @GET("userChatRooms/{uid}.json")
    suspend fun getUserChatRoomList(
        @Path("uid") uid: String?
    ): Map<String, UserChatRoom>

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