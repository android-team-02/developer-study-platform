package com.sesac.developer_study_platform.data.source.remote

import android.util.Log
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StudyRepository {

    private val studyService = StudyService.create()

    suspend fun getStudy(sid: String): Study {
        return studyService.getStudy(sid)
    }

    suspend fun getUserById(uid: String): StudyUser {
        return studyService.getUserById(uid)
    }

    suspend fun getUserStudyList(uid: String?): Map<String, UserStudy> {
        return studyService.getUserStudyList(uid)
    }

    suspend fun getChatRoom(sid: String): ChatRoom {
        return studyService.getChatRoom(sid)
    }

    suspend fun getStudyName(sid: String): String {
        return studyService.getStudy(sid).name
    }

    suspend fun addMessage(sid: String, message: Message) {
        studyService.addMessage(sid, message)
    }

    suspend fun updateReadUserList(sid: String, messageId: String, uid: String?) {
        studyService.updateReadUserList(sid, messageId, mapOf(uid to true))
    }

    suspend fun updateUnreadUserCount(sid: String, uid: String?, count: Int = 0) {
        studyService.updateUnreadUserCount(sid, uid, count)
    }

    suspend fun getStudyMemberList(sid: String): Map<String, Boolean> {
        return studyService.getStudyMemberList(sid)
    }

    suspend fun getUnreadUserList(sid: String): Map<String, Int> {
        return studyService.getUnreadUserList(sid)
    }

    suspend fun updateLastMessage(sid: String, message: Message) {
        studyService.updateLastMessage(sid, message)
    }

    suspend fun getMessage(uid: String?, sid: String): Message {
        return Message(
            uid,
            sid,
            getUser(uid),
            isAdmin(sid, uid),
            getStudyMemberCount(sid),
        )
    }

    private suspend fun getUser(uid: String?): StudyUser? {
        return coroutineScope {
            async {
                kotlin.runCatching {
                    uid?.let { getUserById(it) }
                }.onFailure {
                    Log.e("StudyRepository-getUser", it.message ?: "error occurred.")
                }.getOrNull()
            }.await()
        }
    }

    private suspend fun isAdmin(sid: String, uid: String?): Boolean {
        return coroutineScope {
            async {
                kotlin.runCatching {
                    studyService.isAdmin(sid, uid)
                }.onFailure {
                    Log.e("StudyRepository-isAdmin", it.message ?: "error occurred.")
                }.getOrDefault(false)
            }.await()
        }
    }

    private suspend fun getStudyMemberCount(sid: String): Int {
        return coroutineScope {
            async {
                kotlin.runCatching {
                    studyService.getStudyMemberList(sid).count()
                }.onFailure {
                    Log.e("StudyRepository-getStudyMemberCount", it.message ?: "error occurred.")
                }.getOrDefault(0)
            }.await()
        }
    }

    fun getMessageList(sid: String): Flow<Map<String, Message>> = flow {
        while (true) {
            kotlin.runCatching {
                studyService.getMessageList(sid)
            }.onSuccess {
                emit(it)
            }.onFailure {
                Log.e("StudyRepository-getMessageList", it.message ?: "error occurred.")
            }
        }
    }
}