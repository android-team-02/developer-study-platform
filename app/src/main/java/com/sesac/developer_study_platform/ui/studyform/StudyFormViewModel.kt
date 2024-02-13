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
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
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

    private val _isSelectedImage: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSelectedImage: LiveData<Boolean> = _isSelectedImage

    private val _uploadImageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val uploadImageEvent: LiveData<Event<String>> = _uploadImageEvent

    private val _selectedCategoryEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val selectedCategory: LiveData<Event<String>> = _selectedCategoryEvent

    private val _nameValidationEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val nameValidationEvent: LiveData<Event<Unit>> = _nameValidationEvent

    private val _contentValidationEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val contentValidationEvent: LiveData<Event<Unit>> = _contentValidationEvent

    private val _selectedLanguageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val selectedLanguage: LiveData<Event<String>> = _selectedLanguageEvent

    private val _startDateEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val startDateEvent: LiveData<Event<String>> = _startDateEvent

    private val _endDateEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val endDateEvent: LiveData<Event<String>> = _endDateEvent

    private val _selectedTotalCountEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val selectedTotalCountEvent: LiveData<Event<String>> = _selectedTotalCountEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    fun setImageUri(uri: Uri) {
        _imageUriEvent.value = Event(uri)
        _isSelectedImage.value = true
    }

    fun selectCategory(category: String) {
        _selectedCategoryEvent.value = Event(category)
    }

    fun validateName(name: String) {
        if (name.length == 20) {
            _nameValidationEvent.value = Event(Unit)
        }
    }

    fun validateContent(content: String) {
        if (content.length == 150) {
            _contentValidationEvent.value = Event(Unit)
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

    fun selectTotalCount(language: String) {
        _selectedTotalCountEvent.value = Event(language)
    }

    fun uploadImage(sid: String, name: String, image: Uri) {
        val storageRef = Firebase.storage.reference
            .child("$sid/$name")

        storageRef.putFile(image).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                _uploadImageEvent.value = Event(name)
            }?.addOnFailureListener {
                Log.e("StudyFormViewModel-uploadImage", it.message ?: "error occurred.")
            }
        }.addOnFailureListener {
            Log.e("StudyFormViewModel-uploadImage", it.message ?: "error occurred.")
        }
    }

    suspend fun saveStudy(sid: String, study: Study) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putStudy(sid, study)
            }.onFailure {
                Log.e("StudyFormViewModel-saveStudy", it.message ?: "error occurred.")
            }
        }
    }

    suspend fun saveUserStudy(uid: String, sid: String, userStudy: UserStudy) {
        viewModelScope.launch {
            kotlin.runCatching {
                studyRepository.putUserStudy(uid, sid, userStudy)
            }.onFailure {
                Log.e("StudyFormViewModel-saveUserStudy", it.message ?: "error occurred.")
            }
        }
    }

    fun moveToBack() {
        _moveToBackEvent.value = Event(Unit)
    }

    fun moveToMessage() {
        _moveToMessageEvent.value = Event(Unit)
    }
}