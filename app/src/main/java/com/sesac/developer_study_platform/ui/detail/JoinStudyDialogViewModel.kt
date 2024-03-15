package com.sesac.developer_study_platform.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.fcmRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyGroup
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.data.source.local.FcmTokenRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class JoinStudyDialogViewModel(private val fcmTokenRepository: FcmTokenRepository) : ViewModel() {

    private val _addUserStudyEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val addUserStudyEvent: LiveData<Event<Unit>> = _addUserStudyEvent

    private val _updateStudyGroupEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val updateStudyGroupEvent: LiveData<Event<Unit>> = _updateStudyGroupEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    private val _moveToNotificationPermissionDialogEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToNotificationPermissionDialogEvent: LiveData<Event<String>> =
        _moveToNotificationPermissionDialogEvent

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

    fun updateStudyGroup(sid: String) {
        viewModelScope.launch {
            val token = fcmTokenRepository.getToken().first()
            kotlin.runCatching {
                val notificationKey = getNotificationKey(sid)
                if (!notificationKey.isNullOrEmpty()) {
                    fcmRepository.updateStudyGroup(StudyGroup("add", sid, listOf(token), notificationKey))
                }
            }.onSuccess {
                addRegistrationId(sid, token)
            }.onFailure {
                Log.e("JoinStudyDialogViewModel-updateStudyGroup", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun getNotificationKey(sid: String): String? {
        return viewModelScope.async {
            kotlin.runCatching {
                studyRepository.getNotificationKey(sid)
            }.onFailure {
                Log.e("JoinStudyDialogViewModel-getNotificationKey", it.message ?: "error occurred.")
            }.getOrNull()
        }.await()
    }

    private fun addRegistrationId(sid: String, registrationId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.addRegistrationId(sid, registrationId)
            }.onSuccess {
                _updateStudyGroupEvent.value = Event(Unit)
            }.onFailure {
                Log.e("JoinStudyDialogViewModel-addRegistrationId", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }

    fun moveToNotificationPermissionDialog(sid: String) {
        _moveToNotificationPermissionDialogEvent.value = Event(sid)
    }

    companion object {
        fun create(fcmTokenRepository: FcmTokenRepository) = viewModelFactory {
            initializer {
                JoinStudyDialogViewModel(fcmTokenRepository)
            }
        }
    }
}