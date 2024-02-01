package com.sesac.developer_study_platform.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun stringToUserStudyList(value: String?): List<UserStudy>? {
        return value?.let {
            Json.decodeFromString(it)
        }
    }

    @TypeConverter
    fun userStudyListToString(userStudyList: List<UserStudy>?): String {
        return Json.encodeToString(userStudyList)
    }

    @TypeConverter
    fun stringToStringList(value: String?): List<String>? {
        return value?.let { string ->
            string.split(",").map { it }
        }
    }

    @TypeConverter
    fun stringListToString(stringList: List<String>?): String? {
        return stringList?.joinToString(",")
    }
}