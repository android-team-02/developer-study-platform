package com.sesac.developer_study_platform.data.source.remote

import com.sesac.developer_study_platform.data.User

class GithubRepository {

    private val githubService = GithubService.create()

    suspend fun getUser(accessToken: String): User {
        return githubService.getUser(accessToken)
    }
}