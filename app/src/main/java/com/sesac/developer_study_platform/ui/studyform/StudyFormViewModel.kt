package com.sesac.developer_study_platform.ui.studyform

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.launch

class StudyFormViewModel : ViewModel() {

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    fun saveStudy(sid: String, study: Study) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putStudy(sid, study)
            }.onFailure {
                Log.e("saveStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun saveUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putUserStudy(uid, sid, userStudy)
            }.onFailure {
                Log.e("saveUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }

    fun moveToMessage() {
        _moveToMessageEvent.value = Event(Unit)
    }
}