package com.sesac.developer_study_platform.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sesac.developer_study_platform.Event

class JoinStudyDialogViewModel : ViewModel() {

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }
}