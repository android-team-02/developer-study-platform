package com.sesac.developer_study_platform.ui.mystudy

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

class MyStudyViewModel : ViewModel() {

    private val _userStudyListEvent: MutableLiveData<Event<List<UserStudy>>> = MutableLiveData()
    val userStudyListEvent: LiveData<Event<List<UserStudy>>> = _userStudyListEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    suspend fun loadStudyList() {
        viewModelScope.launch {
            runCatching {
                studyRepository.getUserStudyList(Firebase.auth.uid)
            }.onSuccess {
                _userStudyListEvent.value = Event(it.values.toList())
            }.onFailure {
                Log.e("loadStudyList", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}