package com.sesac.developer_study_platform.ui.studyform

import com.sesac.developer_study_platform.data.DayTime

interface DayTimeClickListener {

    fun onClick(isStartTime: Boolean, dayTime: DayTime)
}