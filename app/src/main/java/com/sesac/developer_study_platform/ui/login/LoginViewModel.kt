package com.sesac.developer_study_platform.ui.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sesac.developer_study_platform.Event
import com.sesac.developer_study_platform.StudyApplication.Companion.githubRepository
import com.sesac.developer_study_platform.StudyApplication.Companion.studyRepository
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.source.local.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) :
    ViewModel() {

    private val _autoLoginEvent: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val autoLoginEvent: LiveData<Event<Boolean>> = _autoLoginEvent

    private val _loginFailureEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val loginFailureEvent: LiveData<Event<Unit>> = _loginFailureEvent

    private val _moveToHomeEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val moveToHome: LiveData<Event<Unit>> = _moveToHomeEvent

    fun checkAutoLogin() {
        viewModelScope.launch {
            _autoLoginEvent.value = Event(userPreferencesRepository.autoLogin.first())
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
                    setAutoLoginTrue()
                }
                .addOnFailureListener {
                    _loginFailureEvent.value = Event(Unit)
                    Log.e("LoginViewModel-startGithubLogin", it.message ?: "error occurred.")
                }
        }
    }

    private fun setAutoLoginTrue() {
        viewModelScope.launch {
            userPreferencesRepository.setAutoLogin(true)
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
        viewModelScope.launch {
            kotlin.runCatching {
                Firebase.auth.uid?.let {
                    studyRepository.putUser(it, user)
                }
            }.onSuccess {
                it?.let {
                    _moveToHomeEvent.value = Event(Unit)
                }
            }.onFailure {
                Log.e("LoginViewModel-saveUser", it.message ?: "error occurred.")
            }
        }
    }
}