package com.theapache64.supergithub

import com.theapache64.supergithub.data.repositories.GitHubRepo
import com.theapache64.supergithub.utils.PathUtils
import kotlin.browser.window

suspend fun main() {
    println("Current window URL is ${window.location}")
    val repoPath = PathUtils.getRepoPath(window.location.href)
    if (repoPath != null) {
        val createdAt = GitHubRepo.getRepo(repoPath)
        println("Created at is ${createdAt.created_at}")
    } else {
        println("It's not a repo")
    }
}