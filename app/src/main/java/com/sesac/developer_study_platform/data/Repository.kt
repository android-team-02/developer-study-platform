package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    val name: String,
    val language: String?,
    val starsCount: Int = 0,
    val forksCount: Int = 0,
    val issuesCount: Int = 0,
    val createdAt: String = "",
)