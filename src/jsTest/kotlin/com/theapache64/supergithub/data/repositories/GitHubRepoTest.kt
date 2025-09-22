package com.theapache64.supergithub.data.repositories

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }

class GitHubRepoTest {

    @Test
    fun getCreateDateForValidRepo() = runTest {
        val repoPath = "theapache64/faded"
        val repo = GitHubRepo.getRepo(repoPath)
        assertEquals("2019-02-11T04:03:06Z", repo.created_at)
    }

    @Test
    fun getCreateDateForInvalidRepo() = runTest {
        val repoPath = ""
        val repo = GitHubRepo.getRepo(repoPath)
        assertNull(repo.created_at)
    }
}