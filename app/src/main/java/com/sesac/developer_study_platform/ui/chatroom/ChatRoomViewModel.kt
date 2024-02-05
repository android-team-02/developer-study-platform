package com.sesac.developer_study_platform.ui.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.launch

class ChatRoomViewModel : ViewModel() {

    private val _chatRoomListEvent: MutableLiveData<Event<List<Pair<UserStudy, ChatRoom>>>> =
        MutableLiveData()
    val chatRoomListEvent: LiveData<Event<List<Pair<UserStudy, ChatRoom>>>> = _chatRoomListEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    suspend fun loadStudyList() {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getUserStudyList(Firebase.auth.uid)
            }.onSuccess {
                loadChatRoomList(it.values.toList())
            }.onFailure {
                Log.e("ChatRoomViewModel-loadStudyList", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun loadChatRoomList(studyList: List<UserStudy>) {
        viewModelScope.launch {
            val chatRoomList = mutableListOf<Pair<UserStudy, ChatRoom>>()
            studyList.forEach { study ->
                kotlin.runCatching {
                    studyRepository.getChatRoom(study.sid)
                }.onSuccess {
                    chatRoomList.add(study to it)
                }.onFailure {
                    Log.e("ChatRoomViewModel-loadChatRoomList", it.message ?: "error occurred.")
                }
            }
            _chatRoomListEvent.value = Event(chatRoomList)
        }
    }

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }
}