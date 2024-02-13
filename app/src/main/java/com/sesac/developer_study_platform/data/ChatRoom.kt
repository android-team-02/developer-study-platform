package com.sesac.developer_study_platform.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val lastMessage: Message = Message(),
    val messages: Map<String, Message> = mapOf(),
    val unreadUsers: Map<String?, Int> = mapOf(Firebase.auth.uid to 0),
)
