package com.theapache64.supergithub.data.repositories

import com.theapache64.supergithub.data.remote.repos.GetRepoResponse
import kotlinx.coroutines.await
import kotlin.browser.window

object GitHubRepo {

    private const val BASE_URL = "https://api.github.com"

    suspend fun getRepo(repoPath: String): GetRepoResponse {
        val url = "$BASE_URL/repos/$repoPath"
        return window.fetch(url)
            .await()
            .json()
            .await()
            .unsafeCast<GetRepoResponse>()
    }
}