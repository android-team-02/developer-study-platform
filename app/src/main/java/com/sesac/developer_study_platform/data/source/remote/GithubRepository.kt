package com.sesac.developer_study_platform.data.source.remote

import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.data.User

class GithubRepository {

    private val githubService = GithubService.create()

    suspend fun getUser(accessToken: String): User {
        return githubService.getUser(accessToken)
    }

    suspend fun getRepositoryList(uid: String): List<Repository> {
        return githubService.getRepositoryList(uid)
    }
}