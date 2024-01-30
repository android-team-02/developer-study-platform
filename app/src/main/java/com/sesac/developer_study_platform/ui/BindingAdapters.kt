package com.sesac.developer_study_platform.ui

import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.util.formatYearMonthDay

@BindingAdapter("dayList")
fun setDayList(view: TextView, dayList: List<String>?) {
    view.text = dayList?.joinToString("\n")
}

@BindingAdapter("isEnabled")
fun setEnabled(view: AppCompatButton, study: Study?) {
    if (study != null) {
        view.isEnabled = !(formatYearMonthDay() > study.endDate
                || study.members.count() == study.totalMemberCount
                || study.banUsers.containsKey(Firebase.auth.uid))
    }
}