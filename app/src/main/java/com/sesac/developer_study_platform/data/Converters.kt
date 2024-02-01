package com.sesac.developer_study_platform.data

import androidx.room.TypeConverter

class Converters {

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