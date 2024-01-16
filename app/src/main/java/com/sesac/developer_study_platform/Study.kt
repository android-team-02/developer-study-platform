package com.sesac.developer_study_platform

import kotlinx.serialization.Serializable

@Serializable
data class Study(
    val banUsers: Map<String, Boolean>,
    val category: String,
    val content: String,
    val createdDate: String,
    val days: Map<String, String>,
    val endDate: String,
    val image: String,
    val language: String,
    val members: Map<String, Boolean>,
    val name: String,
    val sid: String,
    val startDate: String,
    val totalMemberCount: Int,
)