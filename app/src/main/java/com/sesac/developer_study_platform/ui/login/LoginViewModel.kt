package com.sesac.developer_study_platform.ui.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.githubRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.source.local.UserPreferencesRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private var _autoLoginEvent : MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val autoLoginEvent : LiveData<Event<Boolean>> = _autoLoginEvent

    private val _loginFailureEvent : MutableLiveData<Event<Unit>> = MutableLiveData()
    val loginFailureEvent : LiveData<Event<Unit>> = _loginFailureEvent

    private val _moveToHomeEvent : MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToHome : LiveData<Event<Unit>> = _moveToHomeEvent

    fun checkAutoLogin() {
        viewModelScope.launch {
            var isFirst = true
            userPreferencesRepository.autoLogin.collect {
                if (isFirst) {
                    _autoLoginEvent.value = Event(it)
                }
                isFirst = false
            }
        }
    }

    fun startGithubLogin(activity: Activity) {
        val provider = OAuthProvider.newBuilder("github.com")
        val firebaseAuth = Firebase.auth

        if (firebaseAuth.pendingAuthResult == null) {
            firebaseAuth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener {
                    val accessToken = "Bearer ${(it.credential as OAuthCredential).accessToken}"
                    loadUser(accessToken)
                    setAutoLogin()
                }
                .addOnFailureListener {
                    _loginFailureEvent.value = Event(Unit)
                    Log.e("LoginViewModel-startGithubLogin", it.message ?: "error occurred.")
                }
        }
    }

    private fun setAutoLogin() {
        viewModelScope.launch {
            userPreferencesRepository.setAutoLogin()
        }
    }

    private fun loadUser(accessToken: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                githubRepository.getUser(accessToken)
            }.onSuccess {
                saveUser(StudyUser(it.userId, it.image))
            }.onFailure {
                Log.e("LoginViewModel-loadUser", it.message ?: "error occurred.")
            }
        }
    }

    private fun saveUser(user: StudyUser) {
        val uid = Firebase.auth.uid

        viewModelScope.launch {
            kotlin.runCatching {
                uid?.let {
                    studyRepository.putUser(uid, user)
                }
            }.onSuccess {
                _moveToHomeEvent.value = Event(Unit)
            }.onFailure {
                Log.e("LoginViewModel-saveUser", it.message ?: "error occurred.")
            }
        }
    }

    companion object {
        fun create(repository: UserPreferencesRepository) = viewModelFactory {
            initializer {
                LoginViewModel(repository)
            }
        }
    }
}