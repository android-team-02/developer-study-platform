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

    private val _bookmarkStudyListEvent: MutableLiveData<Event<List<BookmarkStudy>>> = MutableLiveData()
    val bookmarkStudyListEvent: LiveData<Event<List<BookmarkStudy>>> = _bookmarkStudyListEvent

    private val _isStudyListEmptyEvent: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isStudyListEmptyEvent: LiveData<Event<Boolean>> = _isStudyListEmptyEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToDetailEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToDetailEvent: LiveData<Event<String>> = _moveToDetailEvent

    fun loadStudyList() {
        viewModelScope.launch {
            kotlin.runCatching {
                bookmarkRepository.getAllBookmarkStudy()
            }.onSuccess {
                _bookmarkStudyListEvent.value = Event(it)
                _isStudyListEmptyEvent.value = Event(it.isEmpty())
            }.onFailure {
                Log.e("BookmarkViewModel-loadStudyList", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToDetail(sid: String) {
        _moveToDetailEvent.value = Event(sid)
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}