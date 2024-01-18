package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    val name: String,
    val language: String?,
    val stargazers_count: Int,
    val forks_count: Int,
    val open_issues_count: Int,
    val created_at: String,
)