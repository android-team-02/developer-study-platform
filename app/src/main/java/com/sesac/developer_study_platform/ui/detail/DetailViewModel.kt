package com.sesac.developer_study_platform.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.Study
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _studyEvent: MutableLiveData<Event<Study>> = MutableLiveData()
    val studyEvent: LiveData<Event<Study>> = _studyEvent

    private val _studyMemberListEvent: MutableLiveData<Event<List<String>>> = MutableLiveData()
    val studyMemberListEvent: LiveData<Event<List<String>>> = _studyMemberListEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToJoinStudyDialogEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToJoinStudyDialogEvent: LiveData<Event<String>> = _moveToJoinStudyDialogEvent

    private var _study: Study? = null
    val study get() = _study!!

    suspend fun loadStudy(sid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getStudy(sid)
            }.onSuccess {
                _studyEvent.value = Event(it)
                _study = it
                loadStudyMemberList(it.members.keys)
            }.onFailure {
                Log.e("DetailViewModel-loadStudy", it.message ?: "error occurred.")
            }
        }
    }

    private suspend fun loadStudyMemberList(uidList: Set<String>) {
        viewModelScope.launch {
            val studyMemberList = mutableListOf<String>()
            uidList.forEach {
                kotlin.runCatching {
                    studyRepository.getUserById(it)
                }.onSuccess {
                    studyMemberList.add(it.userId)
                }.onFailure {
                    Log.e("DetailViewModel-loadStudyMemberList", it.message ?: "error occurred.")
                }
            }
            _studyMemberListEvent.value = Event(studyMemberList)
        }
    }

    suspend fun insertBookmarkStudy(study: Study) {
        viewModelScope.launch {
            bookmarkRepository.insertBookmarkStudy(study)
        }
    }

    suspend fun deleteBookmarkStudyBySid(sid: String) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmarkStudyBySid(sid)
        }
    }

    suspend fun isBookmarkSelected(sid: String): Boolean {
        return viewModelScope.async {
            bookmarkRepository.isBookmarkSelected(sid)
        }.await()
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }

    fun moveToJoinStudyDialog(sid: String) {
        _moveToJoinStudyDialogEvent.value = Event(sid)
    }
}