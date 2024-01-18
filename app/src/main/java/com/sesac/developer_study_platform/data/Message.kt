package com.sesac.developer_study_platform.data

import android.os.Build
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.Date

@Serializable
data class Message(
    val uid: String?,
    val studyUser: StudyUser?,
    val isAdmin: Boolean,
    val message: String,
    val totalMemberCount: Int,
    val readUsers: Map<String?, Boolean>,
) {
    val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().toString()
    } else {
        Date().toString()
    }
}