package com.sesac.developer_study_platform.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.Date
import java.util.Locale

fun String.formatDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(
            this,
            DateTimeFormatter.ofPattern(DateFormats.LOCAL_DATE_TIME_FORMAT.pattern)
        )
        dateTime.format(ISO_LOCAL_DATE)
    } else {
        val dateFormat =
            SimpleDateFormat(DateFormats.LOCAL_DATE_TIME_FORMAT.pattern, Locale.getDefault())
        val newDateFormat =
            SimpleDateFormat(DateFormats.LOCAL_DATE_FORMAT.pattern, Locale.getDefault())
        val date = dateFormat.parse(this) ?: Date()
        newDateFormat.format(date)
    }
}

fun formatYearMonthDay(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(LocalDateTime.now().toString(), ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern(DateFormats.YEAR_MONTH_DAY_FORMAT.pattern)
        dateTime.format(formatter)
    } else {
        val dateFormat =
            SimpleDateFormat(DateFormats.SIMPLE_DATE_TIME_FORMAT.pattern, Locale.getDefault())
        val newDateFormat =
            SimpleDateFormat(DateFormats.YEAR_MONTH_DAY_FORMAT.pattern, Locale.getDefault())
        val date = dateFormat.parse(Date().toString()) ?: Date()
        newDateFormat.format(date)
    }
}

fun formatTimestamp(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateTime = LocalDateTime.parse(LocalDateTime.now().toString(), ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern(DateFormats.TIMESTAMP_FORMAT.pattern)
        dateTime.format(formatter)
    } else {
        val dateFormat =
            SimpleDateFormat(DateFormats.SIMPLE_DATE_TIME_FORMAT.pattern, Locale.getDefault())
        val newDateFormat =
            SimpleDateFormat(DateFormats.TIMESTAMP_FORMAT.pattern, Locale.getDefault())
        val date = dateFormat.parse(Date().toString()) ?: Date()
        newDateFormat.format(date)
    }
}

fun String.formatCalendarDate(): Date {
    val dateFormat =
        SimpleDateFormat(DateFormats.YEAR_MONTH_DAY_FORMAT.pattern, Locale.getDefault())
    return dateFormat.parse(this) as Date
}