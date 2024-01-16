package com.sesac.developer_study_platform.ui

import com.sesac.developer_study_platform.data.DayTime

interface DayTimeClickListener {
    fun onClick(dayTime: DayTime, isStartTime: Boolean)
}