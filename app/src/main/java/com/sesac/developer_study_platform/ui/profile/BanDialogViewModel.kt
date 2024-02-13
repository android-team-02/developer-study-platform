package com.sesac.developer_study_platform.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import kotlinx.coroutines.launch

class BanDialogViewModel : ViewModel() {

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    fun deleteStudyMember(sid: String, uid: String) {
        viewModelScope.launch {
            runCatching {
                studyRepository.deleteStudyMember(sid, uid)
            }.onSuccess {
                deleteUserStudy(sid, uid)
            }.onFailure {
                Log.e("BanDialogViewModel-deleteStudyMember", it.message ?: "error occurred.")
            }
        }
    }

    private fun deleteUserStudy(sid: String, uid: String) {
        viewModelScope.launch {
            runCatching {
                studyRepository.deleteUserStudy(uid, sid)
            }.onSuccess {
                addStudyBanMember(sid, uid)
            }.onFailure {
                Log.e("BanDialogViewModel-deleteUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    private fun addStudyBanMember(sid: String, uid: String) {
        viewModelScope.launch {
            runCatching {
                studyRepository.addStudyBanMember(sid, uid)
            }.onSuccess {
                moveToMessage(sid)
            }.onFailure {
                Log.e("BanDialogViewModel-addStudyBanMember", it.message ?: "error occurred.")
            }
        }
    }

    private fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }
}