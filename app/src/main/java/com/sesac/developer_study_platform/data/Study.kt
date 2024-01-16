package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class Study(
    val sid: String,
    val name: String,
    val image: String,
    val content: String,
    val category: String,
    val language: String,
    val totalMemberCount: Int,
    val days: Map<String, String>,
    val startDate: String,
    val endDate: String,
    val createdDate: String,
    val members: Map<String, Boolean>,
    val banUsers: Map<String, Boolean>,
)

@Serializable
data class UserStudy(
    val sid: String,
    val name: String,
    val image: String,
    val language: String,
    val days: Map<String, String>,
)
