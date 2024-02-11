package com.sesac.developer_study_platform.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.data.source.local.UserPreferencesRepository
import kotlinx.coroutines.launch

class LogoutDialogViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _moveToLoginEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToLoginEvent: LiveData<Event<Unit>> = _moveToLoginEvent

    fun startLogout() {
        Firebase.auth.signOut()
        viewModelScope.launch {
            userPreferencesRepository.setAutoLogin(false)
            moveToLogin()
        }
    }

    private fun moveToLogin() {
        _moveToLoginEvent.value = Event(Unit)
    }

    companion object {
        fun create(repository: UserPreferencesRepository) = viewModelFactory {
            initializer {
                LogoutDialogViewModel(repository)
            }
        }
    }
}