package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class FcmMessage(
    val message: FcmMessageData,
)

@Serializable
data class FcmMessageData(
    val token: String = "",
    val data: FcmMessageContent,
)

@Serializable
data class FcmMessageContent(
    val uid: String? = "",
    val sid: String? = "",
    val title: String = "",
    val text: String = "",
    val imageUrl: String = "",
)