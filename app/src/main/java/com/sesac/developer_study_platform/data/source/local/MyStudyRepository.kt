package com.sesac.developer_study_platform.data.source.local

import androidx.lifecycle.LiveData
import com.sesac.developer_study_platform.StudyApplication.Companion.myStudyDao
import com.sesac.developer_study_platform.data.UserStudy

class MyStudyRepository {

    fun getMyStudyList(): LiveData<List<UserStudy>> {
        return myStudyDao.getAllMyStudies()
    }

    suspend fun insertUserStudy(userStudy: UserStudy) {
        myStudyDao.insertUserStudy(userStudy)
    }
}