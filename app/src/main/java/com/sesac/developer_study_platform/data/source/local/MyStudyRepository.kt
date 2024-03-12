package com.sesac.developer_study_platform.data.source.local

import androidx.lifecycle.LiveData
import com.sesac.developer_study_platform.StudyApplication.Companion.myStudyDao
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.flow.Flow

class MyStudyRepository {

    val myStudyListFlow: Flow<List<UserStudy>> = myStudyDao.getAllMyStudiesFlow()

    fun getMyStudyList(): LiveData<List<UserStudy>> {
        return myStudyDao.getAllMyStudies()
    }

    suspend fun insertUserStudy(userStudy: UserStudy) {
        myStudyDao.insertUserStudy(userStudy)
    }

    suspend fun deleteAllMyStudyList() {
        myStudyDao.deleteAllMyStudies()
    }
}