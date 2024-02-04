package com.sesac.developer_study_platform.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.launch
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyUser

class MyPageViewModel : ViewModel() {

    private val _studyUserEvent: MutableLiveData<Event<StudyUser>> = MutableLiveData()
    val studyUserEvent: LiveData<Event<StudyUser>> = _studyUserEvent

    private val _myStudyListEvent: MutableLiveData<Event<List<UserStudy>>> = MutableLiveData()
    val myStudyListEvent: LiveData<Event<List<UserStudy>>> = _myStudyListEvent

    private val _moveToBookmarkEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBookmarkEvent: LiveData<Event<Unit>> = _moveToBookmarkEvent

    private val _moveToDialogEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToDialogEvent: LiveData<Event<Unit>> = _moveToDialogEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    suspend fun loadUser() {
        viewModelScope.launch {
            runCatching {
                studyRepository.getUserById(Firebase.auth.uid)
            }.onSuccess {
                _studyUserEvent.value = Event(it)
            }.onFailure {
                Log.e("loadUser", it.message ?: "error occurred.")
            }
        }
    }

    suspend fun loadStudyList() {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getUserStudyList(Firebase.auth.uid)
            }.onSuccess {
                _myStudyListEvent.value = Event(it.values.toList())
            }.onFailure {
                Log.e("loadStudyList", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBookmark() {
        _moveToBookmarkEvent.value = Event(Unit)
    }

    fun moveToDialog() {
        _moveToDialogEvent.value = Event(Unit)
    }

    fun moveToMessage() {
        _moveToMessageEvent.value = Event(Unit)
    }
}