package com.sesac.developer_study_platform.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.data.StudyUser
import com.sesac.developer_study_platform.data.User
import com.sesac.developer_study_platform.data.source.remote.GithubService
import com.sesac.developer_study_platform.data.source.remote.StudyService
import com.sesac.developer_study_platform.util.formatDate
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> = _repositories

    private val studyService = StudyService.create()
    private val githubService = GithubService.create()

    fun loadUserData(uid: String?) {
        viewModelScope.launch {
            uid?.let {
                val result = runCatching { studyService.getUserById(uid) }
                result.onSuccess { studyUser ->
                    val user = convertStudyUserToUser(studyUser)
                    _user.value = user
                    Log.d("P1", "User data loaded successfully: $user")
                    user?.userId?.let { userId -> loadRepositories(userId) }
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Error loading user data", exception)
                }
            }
        }
    }

    private fun loadRepositories(userId: String) {
        viewModelScope.launch {
            val result = runCatching { githubService.getRepositoryList(userId) }
            result.onSuccess { repositories ->
                val formattedRepositories = repositories.map { repository ->
                    val formattedDate = repository.createdAt?.formatDate()
                    repository.copy(createdAt = formattedDate)
                }

                _repositories.value = formattedRepositories
            }.onFailure { exception ->
                Log.e("ProfileViewModel", "Error loading repositories", exception)
            }
        }
    }

    private fun convertStudyUserToUser(studyUser: StudyUser?): User? {
        if (studyUser != null) {
            val userId = studyUser.userId
            val image = studyUser.image

            return User(userId, image)
        } else {
            return null
        }
    }
}