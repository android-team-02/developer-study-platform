package com.sesac.developer_study_platform.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.launch

class JoinStudyDialogViewModel : ViewModel() {

    private val _addUserStudyEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val addUserStudyEvent: LiveData<Event<Unit>> = _addUserStudyEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    fun addUserStudy(sid: String, study: UserStudy) {
        viewModelScope.launch {
            kotlin.runCatching {
                Firebase.auth.uid?.let {
                    studyRepository.addUserStudy(it, sid, study)
                }
            }.onSuccess {
                addStudyMember(sid)
            }.onFailure {
                Log.e("JoinStudyDialogViewModel-addUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    private fun addStudyMember(sid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                Firebase.auth.uid?.let {
                    studyRepository.addStudyMember(sid, it)
                }
            }.onSuccess {
                it?.let {
                    _addUserStudyEvent.value = Event(it)
                }
            }.onFailure {
                Log.e("JoinStudyDialogViewModel-addStudyMember", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }
}