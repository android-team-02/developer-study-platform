package com.sesac.developer_study_platform.ui.common

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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationPermissionDialogViewModel(private val fcmTokenRepository: FcmTokenRepository) :
    ViewModel() {

    private val _checkNotificationKeyEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val checkNotificationKeyEvent: LiveData<Event<Unit>> = _checkNotificationKeyEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    fun checkNotificationKey(sid: String) {
        viewModelScope.launch {
            val token = fcmTokenRepository.getToken().first()
            kotlin.runCatching {
                val notificationKey = getNotificationKey(sid)
                if (!notificationKey.isNullOrEmpty()) {
                    updateStudyGroup(sid, token, notificationKey)
                } else {
                    createNotificationKey(sid, token)
                }
            }.onFailure {
                Log.e(
                    "NotificationPermissionDialogViewModel-checkNotificationKey",
                    it.message ?: "error occurred."
                )
            }
        }
    }

    private suspend fun getNotificationKey(sid: String): String? {
        return viewModelScope.async {
            kotlin.runCatching {
                studyRepository.getNotificationKey(sid)
            }.onFailure {
                Log.e(
                    "NotificationPermissionDialogViewModel-getNotificationKey",
                    it.message ?: "error occurred."
                )
            }.getOrNull()
        }.await()
    }

    private fun updateStudyGroup(sid: String, token: String, notificationKey: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                fcmRepository.updateStudyGroup(StudyGroup("add", sid, listOf(token), notificationKey))
            }.onSuccess {
                addRegistrationId(sid, token)
            }.onFailure {
                Log.e(
                    "NotificationPermissionDialogViewModel-updateStudyGroup",
                    it.message ?: "error occurred."
                )
            }
        }
    }

    private fun createNotificationKey(sid: String, token: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                fcmRepository.updateStudyGroup(StudyGroup("create", sid, listOf(token)))
            }.onSuccess {
                addNotificationKey(sid, it.values.first())
            }.onFailure {
                Log.e(
                    "NotificationPermissionDialogViewModel-createNotificationKey",
                    it.message ?: "error occurred."
                )
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
                Log.e(
                    "NotificationPermissionDialogViewModel-addNotificationKey",
                    it.message ?: "error occurred."
                )
            }
        }
    }

    private fun addRegistrationId(sid: String, registrationId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.addRegistrationId(sid, registrationId)
            }.onSuccess {
                _checkNotificationKeyEvent.value = Event(Unit)
            }.onFailure {
                Log.e(
                    "NotificationPermissionDialogViewModel-addRegistrationId",
                    it.message ?: "error occurred."
                )
            }
        }
    }

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }

    companion object {
        fun create(fcmTokenRepository: FcmTokenRepository) = viewModelFactory {
            initializer {
                NotificationPermissionDialogViewModel(fcmTokenRepository)
            }
        }
    }
}