package com.sesac.developer_study_platform

import kotlinx.serialization.Serializable

@Serializable
data class Study(
    val id: String,
    val name: String,
    val image: String,
    val content: String,
    val category: String,
    val language: String,
    val currentMemberCount: Int,
    val totalMemberCount: Int,
    val days: List<String>,
    val startDate: String,
    val endDate: String,
    val createdDate: String,
    val banUsers: List<String>
)
