package com.sesac.developer_study_platform.ui.profile

import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.githubRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.data.StudyUser
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ProfileViewModel : ViewModel() {

    private val _userEvent: MutableLiveData<Event<StudyUser>> = MutableLiveData()
    val userEvent: LiveData<Event<StudyUser>> = _userEvent

    private val _languageListEvent: MutableLiveData<Event<Map<String, String?>>> = MutableLiveData()
    val languageListEvent: LiveData<Event<Map<String, String?>>> = _languageListEvent

    private val _repositoryListEvent: MutableLiveData<Event<List<Repository>>> = MutableLiveData()
    val repositoryListEvent: LiveData<Event<List<Repository>>> = _repositoryListEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    fun loadUser() {
        viewModelScope.launch {
            kotlin.runCatching {
                Firebase.auth.uid?.let {
                    studyRepository.getUserById(it)
                }
            }.onSuccess {
                it?.let {
                    _userEvent.value = Event(it)
                    loadRepositoryList(it.userId)
                }
            }.onFailure {
                Log.e("ProfileViewModel-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    private fun loadRepositoryList(userId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                githubRepository.getRepositoryList(userId)
            }.onSuccess {
                _repositoryListEvent.value = Event(it)
            }.onFailure {
                Log.e("ProfileViewModel-loadRepositoryList", it.message ?: "error occurred.")
            }
        }
    }

    fun parseJson(assetManager: AssetManager) {
        kotlin.runCatching {
            val inputStream = assetManager.open("github-language-colors.json")
            val reader = inputStream.bufferedReader()
            Json.decodeFromString<Map<String, String?>>(reader.readText())
        }.onSuccess {
            _languageListEvent.value = Event(it)
        }.onFailure {
            Log.e("ProfileViewModel-parseJson", it.message ?: "error occurred.")
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}