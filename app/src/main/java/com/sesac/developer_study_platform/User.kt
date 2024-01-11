package com.sesac.developer_study_platform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User (
    @SerialName("avatar_url")
    val image : String,
    val hasAlarm : Boolean = true
)