package com.sesac.developer_study_platform.ui.bookmark

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.bookmarkRepository
import com.sesac.developer_study_platform.data.BookmarkStudy
import kotlinx.coroutines.launch

class BookmarkViewModel : ViewModel() {

    private val _bookmarkStudyListEvent: MutableLiveData<Event<List<BookmarkStudy>>> =
        MutableLiveData()
    val bookmarkStudyListEvent: LiveData<Event<List<BookmarkStudy>>> = _bookmarkStudyListEvent

    private val _emptyStudyListEvent: MutableLiveData<Event<Boolean>> =
        MutableLiveData(Event(false))
    val emptyStudyListEvent: LiveData<Event<Boolean>> = _emptyStudyListEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    suspend fun loadStudyList() {
        viewModelScope.launch {
            kotlin.runCatching {
                bookmarkRepository.getAllBookmarkStudy()
            }.onSuccess {
                _bookmarkStudyListEvent.value = Event(it)
                _emptyStudyListEvent.value = Event(it.isEmpty())
            }.onFailure {
                Log.e("loadStudyList", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}