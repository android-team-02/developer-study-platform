package com.sesac.developer_study_platform.data.source.remote

import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.UserStudy

class StudyRepository {

    private val studyService = StudyService.create()

    suspend fun putUser(uid: String, user: StudyUser) {
        studyService.putUser(uid, user)
    }

    suspend fun getStudy(sid: String): Study {
        return studyService.getStudy(sid)
    }

    suspend fun getUserById(uid: String): StudyUser {
        return studyService.getUserById(uid)
    }

    suspend fun getUserStudyList(uid: String): Map<String, UserStudy> {
        return studyService.getUserStudyList(uid)
    }

    suspend fun getSearchStudyList(searchKeyword: String): Map<String, Study> {
        return studyService.getSearchStudyList("\"${searchKeyword}\"", "\"${searchKeyword}\\uf8ff\"")
    }

    suspend fun getChatRoom(sid: String): ChatRoom {
        return studyService.getChatRoom(sid)
    }
}