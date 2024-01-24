package com.sesac.developer_study_platform.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun String.formatDate(
    localDateTimePattern: String,
    simpleDateFormatPattern: String
): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(
            this,
            DateTimeFormatter.ofPattern(localDateTimePattern)
        )
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        dateTime.format(formatter)
    } else {
        val dateFormat = SimpleDateFormat(simpleDateFormatPattern, Locale.getDefault())
        val newDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val date = dateFormat.parse(this) ?: Date()
        newDateFormat.format(date)
    }
}

fun Set<String>.getDayList(allDayList: List<String>): String {
    val dayList = mutableMapOf<Int, String>()
    this.forEach {
        allDayList.forEachIndexed { index, day ->
            if (it == day) {
                dayList[index] = day
            }
        }
    }
    return dayList.toSortedMap().values.joinToString(", ")
}