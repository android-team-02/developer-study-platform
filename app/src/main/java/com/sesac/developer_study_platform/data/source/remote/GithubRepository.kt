package com.sesac.developer_study_platform.data.source.remote

import com.sesac.developer_study_platform.data.Repository

class GithubRepository {

    private val githubService = GithubService.create()

    suspend fun getRepositoryList(uid: String): List<Repository> {
        return githubService.getRepositoryList(uid)
    }
}