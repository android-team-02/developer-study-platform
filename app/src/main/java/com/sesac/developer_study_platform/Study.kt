package com.sesac.developer_study_platform

data class Study(
    val banList: List<String>,
    val category: String,
    val chatroomId: Int,
    val content: String,
    val createdDate: String,
    val currentPeople: Int,
    val dayList: DayList,
    val endDate: String,
    val id: Int,
    val image: String,
    val language: String,
    val name: String,
    val startDate: String,
    val totalPeople: Int,
    val userIdList: List<String>
)

data class DayList(
    val day: List<String>
) {
    override fun toString(): String {
        return if (day.isNotEmpty()) day.first() else ""
    }
}
