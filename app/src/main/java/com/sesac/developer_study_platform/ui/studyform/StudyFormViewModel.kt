package com.sesac.developer_study_platform.ui.studyform

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.ChatRoom
import com.sesac.developer_study_platform.data.DayTime
import com.sesac.developer_study_platform.data.Study
import com.sesac.developer_study_platform.data.UserStudy
import com.sesac.developer_study_platform.util.DateFormats
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudyFormViewModel : ViewModel() {

    private val _imageUriEvent: MutableLiveData<Event<Uri>> = MutableLiveData()
    val imageUriEvent: LiveData<Event<Uri>> = _imageUriEvent

    private val _isSelectImage: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isSelectImage: LiveData<Event<Boolean>> = _isSelectImage

    private val _categoryEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val categoryEvent: LiveData<Event<String>> = _categoryEvent

    private val _nameEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val nameEvent: LiveData<Event<String>> = _nameEvent

    private val _contentEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val contentEvent: LiveData<Event<String>> = _contentEvent

    private val _isNameValidate: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isNameValidate: LiveData<Event<Boolean>> = _isNameValidate

    private val _isContentValidate: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isContentValidate: LiveData<Event<Boolean>> = _isContentValidate

    private val _languageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val languageEvent: LiveData<Event<String>> = _languageEvent

    private val _startDateEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val startDateEvent: LiveData<Event<String>> = _startDateEvent

    private val _endDateEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val endDateEvent: LiveData<Event<String>> = _endDateEvent

    private val _dayTimeListEvent: MutableLiveData<Event<List<DayTime>>> = MutableLiveData(Event(emptyList()))
    val dayTimeListEvent: LiveData<Event<List<DayTime>>> = _dayTimeListEvent

    private val _dayTimeValidateEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val dayTimeValidateEvent: LiveData<Event<Int>> = _dayTimeValidateEvent

    private val _totalCountEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val totalCountEvent: LiveData<Event<Int>> = _totalCountEvent

    private val _imagePathEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val imagePathEvent: LiveData<Event<String>> = _imagePathEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    fun selectImage(uri: Uri) {
        _imageUriEvent.value = Event(uri)
        _isSelectImage.value = Event(true)
    }

    fun selectCategory(category: String) {
        _categoryEvent.value = Event(category)
    }

    fun nameValidate(name: String) {
        if (name.length == 20) {
            _isNameValidate.value = Event(true)
        } else {
            _nameEvent.value = Event(name)
        }
    }

    fun contentValidate(content: String) {
        if (content.length == 150) {
            _isContentValidate.value = Event(true)
        } else {
            _contentEvent.value = Event(content)
        }
    }

    fun selectLanguage(language: String) {
        _languageEvent.value = Event(language)
    }

    fun selectDateRange(start: Long, end: Long) {
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = start
        _startDateEvent.value = Event(getDate(calendar))

        calendar.timeInMillis = end
        _endDateEvent.value = Event(getDate(calendar))
    }

    private fun getDate(calendar: Calendar): String {
        return SimpleDateFormat(
            DateFormats.YEAR_MONTH_DAY_FORMAT.pattern,
            Locale.getDefault()
        ).format(calendar.time).toString()
    }

    fun validateTime(isStartTime: Boolean, dayTime: DayTime, selectedTime: String) {
        val upDatedList = _dayTimeListEvent.value?.peekContent()?.toMutableList() ?: mutableListOf()
        var foundDayTime = upDatedList.find { it.day == dayTime.day }

        if (foundDayTime == null) {
            foundDayTime = dayTime
            upDatedList.add(foundDayTime)
        }

        if (isStartTime) {
            val endTime = foundDayTime.endTime
            if (endTime != null && selectedTime > endTime) {
                _dayTimeValidateEvent.value = Event(R.string.study_form_validate_start_time)
                return
            }
            foundDayTime.startTime = selectedTime
        } else {
            val startTime = foundDayTime.startTime
            if (startTime != null && selectedTime < startTime) {
                _dayTimeValidateEvent.value = Event(R.string.study_form_validate_end_time)
                return
            }
            foundDayTime.endTime = selectedTime
        }
        _dayTimeListEvent.value = Event(upDatedList.sortedBy { getDaySort(it.day) })
    }

    fun addScheduleForDay(day: String) {
        val upDatedList = _dayTimeListEvent.value?.peekContent()?.toMutableList() ?: mutableListOf()
        val index = upDatedList.indexOfFirst { it.day == day }
        if (index == -1) {
            upDatedList.add(DayTime(day))
        } else {
            upDatedList.removeAt(index)
        }
        val sortedList = upDatedList.sortedBy { getDaySort(it.day) }
        _dayTimeListEvent.value = Event(sortedList)
    }

    private fun getDaySort(day: String): Int {
        return when (day) {
            "월" -> 1
            "화" -> 2
            "수" -> 3
            "목" -> 4
            "금" -> 5
            "토" -> 6
            else -> 7
        }
    }

    fun selectTotalCount(language: Int) {
        _totalCountEvent.value = Event(language)
    }

    fun uploadImage(sid: String, name: String) {
        val storageRef = Firebase.storage.reference
            .child("$sid/$name")

        _imageUriEvent.value?.peekContent()?.let {
            storageRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    _imagePathEvent.value = Event(name)
                }?.addOnFailureListener {
                    Log.e("StudyFormViewModel-uploadImage", it.message ?: "error occurred.")
                }
            }.addOnFailureListener {
                Log.e("StudyFormViewModel-uploadImage", it.message ?: "error occurred.")
            }
        }
    }

    fun saveStudy(sid: String, uid: String, fileName: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putStudy(sid, formatStudy(sid, uid, fileName))
            }.onFailure {
                Log.e("StudyFormViewModel-saveStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun saveUserStudy(sid: String, uid: String, fileName: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putUserStudy(uid, sid, formatUserStudy(sid, fileName))
            }.onFailure {
                Log.e("StudyFormViewModel-saveUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun saveChatRoom(sid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.addChatRoom(sid, ChatRoom())
            }.onSuccess {
                _moveToMessageEvent.value = Event(Unit)
            }.onFailure {
                Log.e("StudyFormViewModel-saveChatRoom", it.message ?: "error occurred.")
            }
        }
    }

    private fun formatStudy(sid: String, uid: String, fileName: String): Study {
        return Study(
            sid = sid,
            name = _nameEvent.value?.peekContent() ?: "",
            image = fileName,
            content = _contentEvent.value?.peekContent() ?: "",
            category = _categoryEvent.value?.peekContent() ?: "",
            language = _languageEvent.value?.peekContent() ?: "",
            totalMemberCount = _totalCountEvent.value?.peekContent() ?: 0,
            days = formatDayTimeList(),
            startDate = _startDateEvent.value?.peekContent() ?: "",
            endDate = _endDateEvent.value?.peekContent() ?: "",
            members = mapOf(uid to true),
            banUsers = mapOf("default" to true)
        )
    }

    private fun formatUserStudy(sid: String, fileName: String): UserStudy {
        return UserStudy(
            sid = sid,
            name = _nameEvent.value?.peekContent() ?: "",
            image = fileName,
            language = _languageEvent.value?.peekContent() ?: "",
            days = formatDayTimeList(),
            startDate = _startDateEvent.value?.peekContent() ?: "",
            endDate = _endDateEvent.value?.peekContent() ?: ""
        )
    }

    private fun formatDayTimeList(): List<String> {
        val list = mutableListOf<String>()
        _dayTimeListEvent.value?.peekContent()?.forEach {
            list.add("${it.day} ${it.startTime}~${it.endTime}")
        }
        return list
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }
}