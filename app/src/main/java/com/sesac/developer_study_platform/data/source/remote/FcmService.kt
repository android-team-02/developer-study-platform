package com.sesac.developer_study_platform.data.source.remote

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sesac.developer_study_platform.BuildConfig
import com.sesac.developer_study_platform.data.FcmMessage
import com.sesac.developer_study_platform.data.StudyGroup
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmService {

    @POST("fcm/notification")
    suspend fun updateStudyGroup(
        @Body studyGroup: StudyGroup
    ): Map<String, String>

    @POST("v1/projects/developer-study-platform/messages:send")
    suspend fun sendNotification(
        @Body message: FcmMessage
    )

    companion object {
        private const val BASE_URL = "https://fcm.googleapis.com"
        private const val SCOPES = "https://www.googleapis.com/auth/firebase.messaging"
        private val contentType = "application/json".toMediaType()
        private val jsonConfig = Json { ignoreUnknownKeys = true }

        private fun getClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor { chain ->
                val builder = chain.request().newBuilder().apply {
                    addHeader("access_token_auth", "true")
                    addHeader("Authorization", "Bearer ${getAccessToken(context)}")
                    addHeader("project_id", BuildConfig.FIREBASE_SENDER_ID)
                }
                chain.proceed(builder.build())
            }.build()
        }

        private fun getAccessToken(context: Context): String {
            val inputStream = context.resources.assets.open("service-account.json")
            val googleCredential = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(listOf(SCOPES))
            googleCredential.refresh()
            return googleCredential.accessToken.tokenValue
        }

        fun create(context: Context): FcmService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient(context))
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(FcmService::class.java)
        }
    }
}