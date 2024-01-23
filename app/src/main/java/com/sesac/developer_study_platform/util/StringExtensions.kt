package com.sesac.developer_study_platform.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun String.formatDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(
            this,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        )
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        dateTime.format(formatter)
    } else {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        val newDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.parse(this) ?: Date()
        newDateFormat.format(date)
    }
}