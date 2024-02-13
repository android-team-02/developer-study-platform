package com.sesac.developer_study_platform.data

import com.sesac.developer_study_platform.ui.message.ViewType
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val uid: String? = "",
    val sid: String = "",
    val studyUser: StudyUser? = null,
    val isAdmin: Boolean = false,
    val totalMemberCount: Int = 0,
    val readUsers: Map<String?, Boolean> = mapOf(uid to true),
    var message: String = "",
    var images: List<String>? = null,
    var type: ViewType = ViewType.MESSAGE,
) {
    var timestamp: Long = System.currentTimeMillis()
}