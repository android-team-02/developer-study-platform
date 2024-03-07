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
            if (getNotificationKey(sid).isNullOrEmpty()) {
                createNotificationKey(sid)
            } else if (isRegistrationId(sid, fcmTokenRepository.getToken().first())) {
                updateStudyGroup(sid)
            }
        }
    }

    private fun createNotificationKey(sid: String) {
        viewModelScope.launch {
            val token = fcmTokenRepository.getToken().first()
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

    private fun updateStudyGroup(sid: String) {
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
                Log.e(
                    "NotificationPermissionDialogViewModel-updateStudyGroup",
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

    private suspend fun isRegistrationId(sid: String, registrationId: String): Boolean {
        return viewModelScope.async {
            kotlin.runCatching {
                studyRepository.getRegistrationIdList(sid)
            }.map {
                it.containsKey(registrationId)
            }.onFailure {
                Log.e(
                    "NotificationPermissionDialogViewModel-isRegistrationId",
                    it.message ?: "error occurred."
                )
            }.getOrDefault(false)
        }.await()
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