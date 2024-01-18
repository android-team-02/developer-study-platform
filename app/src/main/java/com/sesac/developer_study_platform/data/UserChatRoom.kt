package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class UserChatRoom(
    val sid: String,
    val image: String,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: Int,
    val lastVisitTime: Int
)
