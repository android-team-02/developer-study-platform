package com.sesac.developer_study_platform.data

import kotlinx.serialization.Serializable

@Serializable
data class FcmMessage(
    val message: FcmMessageData,
)

@Serializable
data class FcmMessageData(
    val token: String = "",
    val data: Map<String, String> = mapOf(),
)