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
import kotlinx.coroutines.launch

class StudyFormViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Event<Uri>>()
    val imageUri: LiveData<Event<Uri>> = _imageUri

    private val _isSelectedImage: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSelectedImage: LiveData<Boolean> = _isSelectedImage

    private val _uploadImageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val uploadImageEvent: LiveData<Event<String>> = _uploadImageEvent

    private val _nameValidationEvent = MutableLiveData<Event<Unit>>()
    val nameValidationEvent: LiveData<Event<Unit>> = _nameValidationEvent

    private val _contentValidationEvent = MutableLiveData<Event<Unit>>()
    val contentValidationEvent: LiveData<Event<Unit>> = _contentValidationEvent

    private val _moveToBackEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToBackEvent: LiveData<Event<Unit>> = _moveToBackEvent

    private val _moveToMessageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToMessageEvent: LiveData<Event<Unit>> = _moveToMessageEvent

    fun setImageUri(uri: Uri) {
        _imageUri.value = Event(uri)
        _isSelectedImage.value = true
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