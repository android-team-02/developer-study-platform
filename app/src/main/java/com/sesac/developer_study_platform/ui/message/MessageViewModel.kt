package com.sesac.developer_study_platform.ui.message

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.Message
import com.sesac.developer_study_platform.data.StudyUser
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val uid = Firebase.auth.uid

    private val _studyNameEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val studyNameEvent: LiveData<Event<String>> = _studyNameEvent

    private val _messageListEvent: MutableLiveData<Event<Map<String, Message>>> = MutableLiveData()
    val messageListEvent: LiveData<Event<Map<String, Message>>> = _messageListEvent

    private val _addMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val addMessageEvent: LiveData<Event<Unit>> = _addMessageEvent

    private val _addUriListEvent: MutableLiveData<Event<List<Uri>>> = MutableLiveData()
    val addUriListEvent: LiveData<Event<List<Uri>>> = _addUriListEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    fun loadStudyName(sid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getStudyName(sid)
            }.onSuccess {
                _studyNameEvent.value = Event(it)
            }.onFailure {
                Log.e("MessageViewModel-loadStudyName", it.message ?: "error occurred.")
            }
        }
    }

    fun saveMultipleMedia(sid: String, uriList: List<Uri>, timestamp: Long) {
        if (uriList.isNotEmpty()) {
            uriList.forEach {
                kotlin.runCatching {
                    val imagesRef = Firebase.storage.reference
                        .child("${sid}/${uid}/${timestamp}/${it.lastPathSegment}.jpg")
                    imagesRef.putFile(it)
                }.onFailure { exception ->
                    Log.e(
                        "MessageViewModel-saveMultipleMedia",
                        exception.message ?: "error occurred."
                    )
                }
            }
            _addUriListEvent.value = Event(uriList)
        }
    }

    private suspend fun getMessage(uid: String?, sid: String): Message {
        return Message(
            uid,
            sid,
            getUser(uid),
            isAdmin(sid, uid) ?: false,
            getStudyMemberCount(sid),
        )
    }

    private suspend fun getUser(uid: String?): StudyUser? {
        return viewModelScope.async {
            kotlin.runCatching {
                uid?.let {
                    studyRepository.getUserById(it)
                }
            }.onFailure {
                Log.e("MessageViewModel-getUser", it.message ?: "error occurred.")
            }.getOrNull()
        }.await()
    }

    private suspend fun isAdmin(sid: String, uid: String?): Boolean? {
        return viewModelScope.async {
            kotlin.runCatching {
                uid?.let {
                    studyRepository.isAdmin(sid, it)
                }
            }.onFailure {
                Log.e("MessageViewModel-isAdmin", it.message ?: "error occurred.")
            }.getOrDefault(false)
        }.await()
    }

    private suspend fun getStudyMemberCount(sid: String): Int {
        return viewModelScope.async {
            kotlin.runCatching {
                studyRepository.getStudyMemberList(sid).count()
            }.onFailure {
                Log.e("MessageViewModel-getStudyMemberCount", it.message ?: "error occurred.")
            }.getOrDefault(0)
        }.await()
    }

    fun sendImage(sid: String, uriList: List<Uri>, timestamp: Long) {
        viewModelScope.launch {
            val message = getMessage(uid, sid).copy(
                images = uriList.map { it.toString() },
                type = ViewType.IMAGE
            ).apply {
                this.timestamp = timestamp
            }
            kotlin.runCatching {
                studyRepository.addMessage(sid, message)
            }.onSuccess {
                _addMessageEvent.value = Event(Unit)
                loadStudyMemberList(sid)
                updateLastMessage(sid, message)
            }.onFailure {
                Log.e("MessageViewModel-sendImage", it.message ?: "error occurred.")
            }
        }
    }

    fun sendMessage(sid: String, text: String) {
        viewModelScope.launch {
            val message = getMessage(uid, sid).copy(message = text)
            kotlin.runCatching {
                studyRepository.addMessage(sid, message)
            }.onSuccess {
                _addMessageEvent.value = Event(Unit)
                loadStudyMemberList(sid)
                updateLastMessage(sid, message)
            }.onFailure {
                Log.e("MessageViewModel-sendMessage", it.message ?: "error occurred.")
            }
        }
    }

    fun loadMessageList(sid: String) {
        viewModelScope.launch(SupervisorJob()) {
            studyRepository.getMessageList(sid).collect {
                it.keys.forEach { messageId ->
                    updateReadUserList(sid, messageId)
                }
                updateUnreadUserCount(sid)
                _messageListEvent.value = Event(it)
            }
        }
    }

    private fun updateReadUserList(sid: String, messageId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                uid?.let {
                    studyRepository.updateReadUserList(sid, messageId, it)
                }
            }.onFailure {
                Log.e("MessageViewModel-updateReadUserList", it.message ?: "error occurred.")
            }
        }
    }

    private fun updateUnreadUserCount(sid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                uid?.let {
                    studyRepository.updateUnreadUserCount(sid, it)
                }
            }.onFailure {
                Log.e("MessageViewModel-updateUnreadUserCount", it.message ?: "error occurred.")
            }
        }
    }

    private fun loadStudyMemberList(sid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getStudyMemberList(sid)
            }.onSuccess {
                it.keys.forEach { member ->
                    if (member != uid) {
                        loadUnreadUserList(sid, member)
                    }
                }
            }.onFailure {
                Log.e("MessageViewModel-loadStudyMemberList", it.message ?: "error occurred.")
            }
        }
    }

    private fun loadUnreadUserList(sid: String, member: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getUnreadUserList(sid)
            }.onSuccess {
                updateUnreadUserCount(sid, member, it)
            }.onFailure {
                Log.e("MessageViewModel-loadUnreadUserList", it.message ?: "error occurred.")
            }
        }
    }

    private fun updateUnreadUserCount(
        sid: String,
        member: String,
        unreadUserList: Map<String, Int>
    ) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.updateUnreadUserCount(
                    sid,
                    member,
                    unreadUserList.getOrDefault(member, 0) + 1
                )
            }.onFailure {
                Log.e("MessageViewModel-updateUnreadUserCount", it.message ?: "error occurred.")
            }
        }
    }

    private fun updateLastMessage(sid: String, message: Message) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.updateLastMessage(sid, message)
            }.onFailure {
                Log.e("MessageViewModel-updateLastMessage", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}