package com.sesac.developer_study_platform.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sesac.developer_study_platform.Event

class SearchViewModel : ViewModel() {

    private val _moveToSearchResultEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToSearchResultEvent: LiveData<Event<Unit>> = _moveToSearchResultEvent

    private val _moveToCategoryEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToCategoryEvent: LiveData<Event<String>> = _moveToCategoryEvent

    fun moveToSearchResult() {
        _moveToSearchResultEvent.value = Event(Unit)
    }

    fun moveToCategory(category: String) {
        _moveToCategoryEvent.value = Event(category)
    }
}