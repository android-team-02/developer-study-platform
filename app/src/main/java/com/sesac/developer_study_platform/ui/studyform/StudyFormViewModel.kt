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

    private val _isSelectedImage: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isSelectedImage: LiveData<Event<Boolean>> = _isSelectedImage

    private val _uploadImageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val uploadImageEvent: LiveData<Event<String>> = _uploadImageEvent

    private val _selectedCategoryEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val selectedCategory: LiveData<Event<String>> = _selectedCategoryEvent

    private val _nameEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val nameEvent: LiveData<Event<String>> = _nameEvent

    private val _contentEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val contentEvent: LiveData<Event<String>> = _contentEvent

    private val _isNameValidate: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isNameValidate: LiveData<Event<Boolean>> = _isNameValidate

    private val _isContentValidate: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isContentValidate: LiveData<Event<Boolean>> = _isContentValidate

    private val _selectedLanguageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val selectedLanguage: LiveData<Event<String>> = _selectedLanguageEvent

    private val _startDateEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val startDateEvent: LiveData<Event<String>> = _startDateEvent

    private val _endDateEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val endDateEvent: LiveData<Event<String>> = _endDateEvent

    private val _dayTimeListEvent: MutableLiveData<Event<List<DayTime>>> = MutableLiveData(Event(emptyList()))
    val dayTimeListEvent: LiveData<Event<List<DayTime>>> = _dayTimeListEvent

    private val _dayTimeErrorMessageEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val dayTimeErrorMessageEvent: LiveData<Event<Int>> = _dayTimeErrorMessageEvent

    private val _selectedTotalCountEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val selectedTotalCountEvent: LiveData<Event<Int>> = _selectedTotalCountEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    fun setImageUri(uri: Uri) {
        _imageUriEvent.value = Event(uri)
        _isSelectedImage.value = Event(true)
    }

    fun selectCategory(category: String) {
        _selectedCategoryEvent.value = Event(category)
    }

    fun validateName(name: String) {
        if (name.length == 20) {
            _isNameValidate.value = Event(true)
        } else {
            _nameEvent.value = Event(name)
        }
    }

    fun validateContent(content: String) {
        if (content.length == 150) {
            _isContentValidate.value = Event(true)
        } else {
            _contentEvent.value = Event(content)
        }
    }

    fun selectLanguage(language: String) {
        _selectedLanguageEvent.value = Event(language)
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
                _dayTimeErrorMessageEvent.value = Event(R.string.study_form_validate_start_time)
                return
            }
            foundDayTime.startTime = selectedTime
        } else {
            val startTime = foundDayTime.startTime
            if (startTime != null && selectedTime < startTime) {
                _dayTimeErrorMessageEvent.value = Event(R.string.study_form_validate_end_time)
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
        _selectedTotalCountEvent.value = Event(language)
    }

    fun uploadImage(sid: String, name: String) {
        val storageRef = Firebase.storage.reference
            .child("$sid/$name")

        _imageUriEvent.value?.peekContent()?.let {
            storageRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    _uploadImageEvent.value = Event(name)
                }?.addOnFailureListener {
                    Log.e("StudyFormViewModel-uploadImage", it.message ?: "error occurred.")
                }
            }.addOnFailureListener {
                Log.e("StudyFormViewModel-uploadImage", it.message ?: "error occurred.")
            }
        }
    }

    fun saveStudy(sid: String, study: Study) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putStudy(sid, study)
            }.onFailure {
                Log.e("StudyFormViewModel-saveStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun saveUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putUserStudy(uid, sid, userStudy)
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

    fun formatStudy(sid: String, uid: String, fileName: String): Study {
        return Study(
            sid = sid,
            name = _nameEvent.value?.peekContent() ?: "",
            image = fileName,
            content = _contentEvent.value?.peekContent() ?: "",
            category = _selectedCategoryEvent.value?.peekContent() ?: "",
            language = _selectedLanguageEvent.value?.peekContent() ?: "",
            totalMemberCount = _selectedTotalCountEvent.value?.peekContent() ?: 0,
            days = formatDayTimeList(),
            startDate = _startDateEvent.value?.peekContent() ?: "",
            endDate = _endDateEvent.value?.peekContent() ?: "",
            members = mapOf(uid to true),
            banUsers = mapOf("default" to true)
        )
    }

    fun formatUserStudy(sid: String, fileName: String): UserStudy {
        return UserStudy(
            sid = sid,
            name = _nameEvent.value?.peekContent() ?: "",
            image = fileName,
            language = _selectedLanguageEvent.value?.peekContent() ?: "",
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