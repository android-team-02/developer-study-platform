package com.sesac.developer_study_platform.data.source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sesac.developer_study_platform.BuildConfig
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyService {

    @PUT("users/{uid}.json")
    suspend fun putUser(
        @Path("uid") uid: String,
        @Body user: StudyUser
    )

    @PUT("studies/{sid}.json")
    suspend fun putStudy(
        @Path("sid") sid: String,
        @Body study: Study
    )

    @PUT("userStudyRooms/{uid}/{sid}.json")
    suspend fun putUserStudy(
        @Path("uid") uid: String,
        @Path("sid") sid: String,
        @Body userStudy: UserStudy
    )

    @GET("userStudyRooms/{uid}.json")
    suspend fun getUserStudyList(
        @Path("uid") uid: String
    ): Map<String, UserStudy>

    @GET("studies.json")
    suspend fun getStudyList(
        @Query("equalTo") category: String,
        @Query("orderBy") orderBy: String = "\"category\""
    ): Map<String, Study>

    @GET("studies.json")
    suspend fun getSearchStudyList(
        @Query("startAt") startAt: String,
        @Query("endAt") endAt: String,
        @Query("orderBy") orderBy: String = "\"name\""
    ): Map<String, Study>

    @GET("studies/{sid}.json")
    suspend fun getStudy(
        @Path("sid") sid: String
    ): Study

    @GET("users/{uid}.json")
    suspend fun getUserById(
        @Path("uid") uid: String?,
    ): StudyUser

    @GET("chatRooms/{chatRoomId}/messages.json")
    suspend fun getMessageList(
        @Path("chatRoomId") chatRoomId: String
    ): Map<String, Message>

    @PATCH("chatRooms/{chatRoomId}/messages/{messageId}/readUsers.json")
    suspend fun updateReadUserList(
        @Path("chatRoomId") chatRoomId: String,
        @Path("messageId") messageId: String,
        @Body readUsers: Map<String, Boolean>
    )

    @PUT("chatRooms/{chatRoomId}/unreadUsers/{uid}.json")
    suspend fun updateUnreadUserCount(
        @Path("chatRoomId") chatRoomId: String,
        @Path("uid") uid: String,
        @Body count: Int = 0
    )

    @GET("studies/{studyId}/members/{uid}.json")
    suspend fun isAdmin(
        @Path("studyId") studyId: String,
        @Path("uid") uid: String
    ): Boolean

    @GET("studies/{studyId}/members.json")
    suspend fun getStudyMemberList(
        @Path("studyId") studyId: String
    ): Map<String, Boolean>

    @GET("chatRooms/{chatRoomId}/unreadUsers.json")
    suspend fun getUnreadUserList(
        @Path("chatRoomId") chatRoomId: String
    ): Map<String, Int>

    @POST("chatRooms/{chatRoomId}/messages.json")
    suspend fun addMessage(
        @Path("chatRoomId") chatRoomId: String,
        @Body message: Message,
    )

    @PUT("chatRooms/{chatRoomId}/lastMessage.json")
    suspend fun updateLastMessage(
        @Path("chatRoomId") chatRoomId: String,
        @Body lastMessage: Message
    )

    @GET("chatRooms/{chatRoomId}.json")
    suspend fun getChatRoom(
        @Path("chatRoomId") chatRoomId: String
    ): ChatRoom

    @PATCH("studies/{sid}/members.json")
    suspend fun addStudyMember(
        @Path("sid") sid: String,
        @Body members: Map<String, Boolean>
    )

    @PUT("chatRooms/{chatRoomId}.json")
    suspend fun addChatRoom(
        @Path("chatRoomId") chatRoomId: String,
        @Body chatRoom: ChatRoom
    )

    @DELETE("studies/{sid}/members/{uid}.json")
    suspend fun deleteStudyMember(
        @Path("sid") sid: String,
        @Path("uid") uid: String
    )

    @DELETE("userStudyRooms/{uid}/{sid}.json")
    suspend fun deleteUserStudy(
        @Path("uid") uid: String,
        @Path("sid") sid: String
    )

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