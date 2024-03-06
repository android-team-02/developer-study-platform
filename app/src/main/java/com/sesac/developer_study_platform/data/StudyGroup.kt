package com.sesac.developer_study_platform.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyGroup(
    val operation: String = "",
    @SerialName("notification_key_name") val notificationKeyName: String = "",
    @SerialName("registration_ids") val registrationIdList: List<String> = listOf(),
    @SerialName("notification_key") val notificationKey: String = "",
)