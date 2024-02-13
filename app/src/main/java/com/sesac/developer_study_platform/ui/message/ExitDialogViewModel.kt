package com.sesac.developer_study_platform.ui.message

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import kotlinx.coroutines.launch

class ExitDialogViewModel: ViewModel() {

    private val uid = Firebase.auth.uid

    private val _moveToHomeEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToHomeEvent: LiveData<Event<Unit>> = _moveToHomeEvent

   fun deleteStudyMember(sid: String) {
        viewModelScope.launch {
            runCatching {
                uid?.let {
                    studyRepository.deleteStudyMember(sid, uid)
                }
            }.onSuccess {
                deleteUserStudy(sid)
            }.onFailure {
                Log.e("ExitDialogViewModel-deleteStudyMember", it.message ?: "error occurred.")
            }
        }
    }

    private fun deleteUserStudy(sid: String) {
        viewModelScope.launch {
            runCatching {
                uid?.let {
                    studyRepository.deleteUserStudy(uid, sid)
                }
            }.onSuccess {
                _moveToHomeEvent.value = Event(Unit)
            }.onFailure {
                Log.e("ExitDialogViewModel-deleteUserStudy", it.message ?: "error occurred.")
            }
        }
    }
}