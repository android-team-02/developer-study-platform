package com.sesac.developer_study_platform.data.source.remote

import android.content.Context
import com.sesac.developer_study_platform.data.FcmMessage
import com.sesac.developer_study_platform.data.StudyGroup

class FcmRepository(context: Context) {

    private val fcmService = FcmService.create(context)

    suspend fun updateStudyGroup(studyGroup: StudyGroup): Map<String, String> {
        return fcmService.updateStudyGroup(studyGroup)
    }

    suspend fun sendNotification(message: FcmMessage) {
        fcmService.sendNotification(message)
    }
}