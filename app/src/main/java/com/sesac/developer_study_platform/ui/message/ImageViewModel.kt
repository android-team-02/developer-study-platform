package com.sesac.developer_study_platform.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sesac.developer_study_platform.Event

class ImageViewModel : ViewModel() {

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}