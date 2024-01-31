package com.sesac.developer_study_platform.data.source.remote

import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.UserStudy

class StudyRepository {

    private val studyService = StudyService.create()

    suspend fun getStudy(sid: String): Study {
        return studyService.getStudy(sid)
    }

    suspend fun getUserById(uid: String): StudyUser {
        return studyService.getUserById(uid)
    }

    suspend fun getUserStudyList(uid: String): Map<String, UserStudy> {
        return studyService.getUserStudyList(uid)
    }
}