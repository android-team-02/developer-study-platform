package com.sesac.developer_study_platform.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.Date
import java.util.Locale

fun String.formatTime(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(this, ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        dateTime.format(formatter)
    } else {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        val newDateFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
        val date = dateFormat.parse(this) ?: Date()
        newDateFormat.format(date)
    }
}

fun String.formatDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(this, ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        dateTime.format(formatter)
    } else {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        val newDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val date = dateFormat.parse(this) ?: Date()
        newDateFormat.format(date)
    }
}