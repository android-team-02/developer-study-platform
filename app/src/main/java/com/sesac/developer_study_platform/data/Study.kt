package com.sesac.developer_study_platform.data

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val days: List<String>,
    val startDate: String,
    val endDate: String,
    val members: Map<String, Boolean>,
    val banUsers: Map<String, Boolean>,
)

@Entity(tableName = "my_studies")
@Serializable
data class UserStudy(
    @PrimaryKey val sid: String,
    val name: String,
    val image: String,
    val language: String,
    val days: List<String>,
    val startDate: String,
    val endDate: String,
)

@Entity(tableName = "bookmark_studies")
data class BookmarkStudy(
    val sid: String,
    val name: String,
    val image: String,
    val language: String,
    val days: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
)