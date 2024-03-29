package com.sesac.developer_study_platform.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.myStudyRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException

@OptIn(FlowPreview::class)
class HomeViewModel : ViewModel() {

    private val _myStudyListEvent: MutableLiveData<Event<List<UserStudy>>> = MutableLiveData()
    val myStudyListEvent: LiveData<Event<List<UserStudy>>> = _myStudyListEvent

    private val _studyFormButtonEvent: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val studyFormButtonEvent: LiveData<Event<Boolean>> = _studyFormButtonEvent

    private val _moveToMyStudyEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMyStudyEvent: LiveData<Event<Unit>> = _moveToMyStudyEvent

    private val _moveToStudyFormEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToStudyFormEvent: LiveData<Event<Unit>> = _moveToStudyFormEvent

    private val _moveToCategoryEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToCategoryEvent: LiveData<Event<String>> = _moveToCategoryEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    init {
        viewModelScope.launch {
            val myStudyList = myStudyRepository.myStudyListFlow.first()
            _myStudyListEvent.value = Event(myStudyList)
            _studyFormButtonEvent.value = Event(myStudyList.isEmpty())

            myStudyRepository.myStudyListFlow.debounce(500).collect {
                _myStudyListEvent.value = Event(it)
            }
        }
    }

    fun loadStudyList() {
        viewModelScope.launch {
            kotlin.runCatching {
                Firebase.auth.uid?.let {
                    studyRepository.getUserStudyList(it)
                }
            }.onSuccess {
                it?.let {
                    refreshAllMyStudyList(it.values.toList())
                    _studyFormButtonEvent.value = Event(false)
                }
            }.onFailure {
                if (it is SerializationException) {
                    refreshAllMyStudyList(emptyList())
                    _studyFormButtonEvent.value = Event(true)
                } else {
                    Log.e("HomeViewModel-loadStudyList", it.message ?: "error occurred.")
                }
            }
        }
    }

    private fun refreshAllMyStudyList(userStudyList: List<UserStudy>) {
        viewModelScope.launch {
            runCatching {
                myStudyRepository.deleteAllMyStudyList()
            }.onSuccess {
                insertUserStudy(userStudyList)
            }.onFailure {
                Log.e("HomeViewModel-deleteAllMyStudyList", it.message ?: "error occurred.")
            }
        }
    }

    private fun insertUserStudy(userStudyList: List<UserStudy>) {
        userStudyList.forEach {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("${it.sid}/${it.image}")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                viewModelScope.launch {
                    myStudyRepository.insertUserStudy(it.copy(image = uri.toString()))
                }
            }.addOnFailureListener {
                Log.e("HomeViewModel-insertUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToMyStudy() {
        _moveToMyStudyEvent.value = Event(Unit)
    }

    fun moveToStudyForm() {
        _moveToStudyFormEvent.value = Event(Unit)
    }

    fun moveToCategory(category: String) {
        _moveToCategoryEvent.value = Event(category)
    }

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }
}