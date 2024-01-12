package com.sesac.developer_study_platform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User (
    @SerialName("login")
    val userId : String,
    @SerialName("avatar_url")
    val image : String
)

@Serializable
data class StudyUser (
    val userId: String,
    val image: String
)