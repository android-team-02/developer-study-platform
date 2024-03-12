package com.sesac.developer_study_platform.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.myStudyRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.util.formatCalendarDate
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyPageViewModel : ViewModel() {

    private val _studyUserEvent: MutableLiveData<Event<StudyUser>> = MutableLiveData()
    val studyUserEvent: LiveData<Event<StudyUser>> = _studyUserEvent

    private val _dotSpanDayListEvent: MutableLiveData<Event<Set<CalendarDay>>> = MutableLiveData()
    val dotSpanDayListEvent: LiveData<Event<Set<CalendarDay>>> = _dotSpanDayListEvent

    private val _selectedDayStudyListEvent: MutableLiveData<Event<List<UserStudy>>> = MutableLiveData()
    val selectedDayStudyListEvent: LiveData<Event<List<UserStudy>>> = _selectedDayStudyListEvent

    private val _isSelectedDayEmpty: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSelectedDayEmpty: LiveData<Boolean> = _isSelectedDayEmpty

    private val _moveToBookmarkEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBookmarkEvent: LiveData<Event<Unit>> = _moveToBookmarkEvent

    private val _moveToDialogEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToDialogEvent: LiveData<Event<Unit>> = _moveToDialogEvent

    private val _moveToMessageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<String>> = _moveToMessageEvent

    private val calendar = Calendar.getInstance()
    private val uid = Firebase.auth.uid

    val myStudyList: LiveData<List<UserStudy>> = myStudyRepository.getMyStudyList()
    var studyList = listOf<UserStudy>()

    fun loadUser() {
        viewModelScope.launch {
            kotlin.runCatching {
                uid?.let {
                    studyRepository.getUserById(it)
                }
            }.onSuccess {
                it?.let {
                    _studyUserEvent.value = Event(it)
                }
            }.onFailure {
                Log.e("MyPageViewModel-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    fun setDotSpanDayList(studyList: List<UserStudy>) {
        val allDayList = mutableSetOf<CalendarDay>()
        studyList.forEach {
            val dayList = getDotSpanDayList(
                it.startDate.formatCalendarDate(),
                it.endDate.formatCalendarDate(),
                formatDayList(it.days)
            )
            allDayList.addAll(dayList)
        }
        _dotSpanDayListEvent.value = Event(allDayList)
    }

    private fun getDotSpanDayList(startDate: Date, endDate: Date, days: List<String>): List<CalendarDay> {
        val dotSpanDayList = mutableListOf<CalendarDay>()
        calendar.time = startDate
        while (calendar.time <= endDate) {
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREAN) ?: ""
            if (days.contains(dayOfWeek)) {
                dotSpanDayList.add(
                    CalendarDay.from(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                )
            }
            calendar.add(Calendar.DATE, 1)
        }
        return dotSpanDayList
    }

    fun setSelectedDayStudyList(calendarDay: CalendarDay) {
        val formatCalendarDay = formatCalendarDay(calendarDay)
        val studyList = studyList.filter {
            val startDate = it.startDate.formatCalendarDate()
            val endDate = it.endDate.formatCalendarDate()
            val selectedDate =
                "${calendarDay.year}/${calendarDay.month}/${calendarDay.day}".formatCalendarDate()
            val isInDateRange = selectedDate in startDate..endDate
            val isConcurDay = formatDayList(it.days).contains(formatCalendarDay)
            isInDateRange && isConcurDay
        }
        _selectedDayStudyListEvent.value = Event(studyList)
    }

    private fun formatCalendarDay(date: CalendarDay): String {
        calendar.set(date.year, date.month - 1, date.day)
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            Calendar.SUNDAY -> "일"
            else -> ""
        }
    }

    private fun formatDayList(days: List<String>): List<String> {
        return days.map { it.substringBefore(" ") }.toList()
    }

    fun moveToBookmark() {
        _moveToBookmarkEvent.value = Event(Unit)
    }

    fun moveToDialog() {
        _moveToDialogEvent.value = Event(Unit)
    }

    fun moveToMessage(sid: String) {
        _moveToMessageEvent.value = Event(sid)
    }
}