package com.sesac.developer_study_platform.ui.studyform

import android.text.Editable
import android.text.TextWatcher

class CustomTextWatcher(private val temp: (String) -> Unit) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        temp(s.toString())
    }
}