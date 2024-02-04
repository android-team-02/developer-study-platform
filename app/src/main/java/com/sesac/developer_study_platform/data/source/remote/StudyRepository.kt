package com.sesac.developer_study_platform.data.source.remote

import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.User
import com.sesac.developer_study_platform.data.UserStudy
import retrofit2.http.Body

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

    suspend fun putStudy(sid: String, study: Study) {
        return studyService.putStudy(sid, study)
    }

    suspend fun putUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        return studyService.putUserStudy(uid, sid, userStudy)
    }
}