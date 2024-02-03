package com.sesac.developer_study_platform.ui.searchresult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.Study
import kotlinx.coroutines.launch

class SearchResultViewModel : ViewModel() {

    private val _searchStudyListEvent: MutableLiveData<Event<List<Study>>> = MutableLiveData()
    val searchStudyListEvent: LiveData<Event<List<Study>>> = _searchStudyListEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    suspend fun loadStudyList(searchKeyword: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getSearchStudyList(searchKeyword)
            }.onSuccess {
                _searchStudyListEvent.value = Event(it.values.toList())
            }.onFailure {
                Log.e("SearchResultViewModel", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}