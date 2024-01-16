package com.sesac.developer_study_platform

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val image: String,
    val userId: String,
)