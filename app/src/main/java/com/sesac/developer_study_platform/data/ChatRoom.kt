package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val lastMessage: Message,
    val messages: Map<String, Message>,
    val unreadUsers: Map<String, Int>,
)
