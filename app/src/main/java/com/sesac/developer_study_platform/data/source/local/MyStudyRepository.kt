package com.sesac.developer_study_platform.data.source.local

import com.sesac.developer_study_platform.StudyApplication.Companion.myStudyDao
import com.sesac.developer_study_platform.data.UserStudy

class MyStudyRepository {

    suspend fun getMyStudyList(): List<UserStudy> {
        return myStudyDao.getAllMyStudies()
    }

    suspend fun refreshMyStudyList(myStudyList: List<UserStudy>) {
        myStudyDao.deleteAllMyStudies()
        myStudyList.forEach {
            myStudyDao.insertUserStudy(it)
        }
    }
}