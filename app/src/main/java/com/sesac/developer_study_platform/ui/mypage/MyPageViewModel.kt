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
import com.sesac.developer_study_platform.data.UserStudy
import kotlinx.coroutines.launch
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.util.formatCalendarDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyPageViewModel : ViewModel() {

    private val _studyUserEvent: MutableLiveData<Event<StudyUser>> = MutableLiveData()
    val studyUserEvent: LiveData<Event<StudyUser>> = _studyUserEvent

    private val _dotSpanAllDays = MutableLiveData<Event<Set<CalendarDay>>>()
    val dotSpanAllDays: LiveData<Event<Set<CalendarDay>>> = _dotSpanAllDays

    private val _selectedDayStudy = MutableLiveData<Event<List<UserStudy>>>()
    val selectedDayStudy: LiveData<Event<List<UserStudy>>> = _selectedDayStudy

    private val _moveToBookmarkEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBookmarkEvent: LiveData<Event<Unit>> = _moveToBookmarkEvent

    private val _moveToDialogEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToDialogEvent: LiveData<Event<Unit>> = _moveToDialogEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    private val studyList = mutableListOf<UserStudy>()
    private val calendar = Calendar.getInstance()

    suspend fun loadUser() {
        viewModelScope.launch {
            runCatching {
                studyRepository.getUserById(Firebase.auth.uid)
            }.onSuccess {
                _studyUserEvent.value = Event(it)
            }.onFailure {
                Log.e("MyPageViewModel-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    suspend fun loadStudyList() {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.getUserStudyList(Firebase.auth.uid)
            }.onSuccess {
                setDaysDotSpan(it.values.toList())
                studyList.addAll(it.values.toList())
            }.onFailure {
                Log.e("MyPageViewModel-loadStudyList", it.message ?: "error occurred.")
            }
        }
    }

    private fun setDaysDotSpan(studyList: List<UserStudy>) {
        val allDayList = mutableSetOf<CalendarDay>()
        studyList.forEach {
            val days = getDotSpanDayList(
                it.startDate.formatCalendarDate(),
                it.endDate.formatCalendarDate(),
                formatDays(it.days)
            )
            allDayList.addAll(days)
        }
        _dotSpanAllDays.value = Event(allDayList)
    }

    private fun getDotSpanDayList(startDate: Date, endDate: Date, days: List<String>): List<CalendarDay> {
        val dotSpanDayList = ArrayList<CalendarDay>()
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

    fun getStudyList(calendarDay: CalendarDay) {
        val formatCalendarDay = getDayList(calendarDay)
        val studyList = studyList.filter {
            val startDate = it.startDate.formatCalendarDate()
            val endDate = it.endDate.formatCalendarDate()
            val selectedDate = "${calendarDay.year}/${calendarDay.month}/${calendarDay.day}".formatCalendarDate()

            val isInDateRange = selectedDate in startDate..endDate
            val isConcurDay = formatDays(it.days).contains(formatCalendarDay)
            isInDateRange && isConcurDay
        }
        _selectedDayStudy.value = Event(studyList)
    }

    private fun getDayList(date: CalendarDay): String {
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

    private fun formatDays(days: List<String>): List<String> {
        return days.map { it.substringBefore(" ") }.toList()
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