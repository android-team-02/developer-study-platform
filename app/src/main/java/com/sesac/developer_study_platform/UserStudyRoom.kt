package com.sesac.developer_study_platform

import kotlinx.serialization.Serializable

@Serializable
data class UserStudyRoom(
    val days: Map<String, String>,
    val image: String,
    val language: String,
    val name: String,
    val sid: String,
)
