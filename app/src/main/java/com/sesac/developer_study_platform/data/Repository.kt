package com.sesac.developer_study_platform.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    val name: String,
    val language: String?,
    @SerialName("stargazers_count") val star: Int,
    @SerialName("forks_count") val fork: Int,
    @SerialName("open_issues_count") val issue: Int,
    @SerialName("created_at") val createdAt: String?,
)