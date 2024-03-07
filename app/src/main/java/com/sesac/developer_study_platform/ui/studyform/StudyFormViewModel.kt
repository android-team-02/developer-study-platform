package com.sesac.developer_study_platform.ui.studyform

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.fcmRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyGroup
import com.sesac.developer_study_platform.data.source.local.FcmTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StudyFormViewModel(private val fcmTokenRepository: FcmTokenRepository) : ViewModel() {

    private val _createNotificationKeyEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val createNotificationKeyEvent: LiveData<Event<Unit>> = _createNotificationKeyEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    private val _moveToNotificationPermissionDialogEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToNotificationPermissionDialogEvent: LiveData<Event<String>> =
        _moveToNotificationPermissionDialogEvent

    fun createNotificationKey(sid: String) {
        viewModelScope.launch {
            val token = fcmTokenRepository.getToken().first()
            kotlin.runCatching {
                fcmRepository.updateStudyGroup(StudyGroup("create", sid, listOf(token)))
            }.onSuccess {
                addNotificationKey(sid, it.values.first())
            }.onFailure {
                Log.e("StudyFormViewModel-createNotificationKey", it.message ?: "error occurred.")
            }
        }
    }

    private fun addNotificationKey(sid: String, notificationKey: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.addNotificationKey(sid, notificationKey)
            }.onSuccess {
                addRegistrationId(sid, fcmTokenRepository.getToken().first())
            }.onFailure {
                Log.e("StudyFormViewModel-addNotificationKey", it.message ?: "error occurred.")
            }
        }
    }

    private fun addRegistrationId(sid: String, registrationId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.addRegistrationId(sid, registrationId)
            }.onSuccess {
                _createNotificationKeyEvent.value = Event(Unit)
            }.onFailure {
                Log.e("StudyFormViewModel-addRegistrationId", it.message ?: "error occurred.")
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
                StudyFormViewModel(fcmTokenRepository)
            }
        }
    }
}