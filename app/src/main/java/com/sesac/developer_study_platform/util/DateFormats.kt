package com.sesac.developer_study_platform.util

enum class DateFormats(val pattern: String) {
    LOCAL_DATE_TIME_FORMAT("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    SIMPLE_DATE_TIME_FORMAT("EEE MMM dd HH:mm:ss z yyyy"),
    LOCAL_DATE_FORMAT("yyyy-MM-dd"),
    YEAR_MONTH_DAY_FORMAT("yyyy/MM/dd"),
    TIMESTAMP_FORMAT("yyyyMMddHHmmss"),
    TIME_FORMAT("a hh:mm"),
    SYSTEM_MESSAGE_FORMAT("yyyy년 MM월 dd일"),
}