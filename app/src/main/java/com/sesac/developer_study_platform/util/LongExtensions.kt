package com.sesac.developer_study_platform.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.convertTimestampToDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
        DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentDateTime)
    } else {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
    }
}

fun Long.convertTimestampToTime(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
        DateTimeFormatter.ofPattern("a hh:mm").format(currentDateTime)
    } else {
        SimpleDateFormat("a hh:mm", Locale.getDefault()).format(this)
    }
}