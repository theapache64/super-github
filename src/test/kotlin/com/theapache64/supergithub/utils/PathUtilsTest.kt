package com.theapache64.supergithub.utils

import kotlin.test.Test
import kotlin.test.assertEquals


internal class PathUtilsTest {
    @Test
    fun getRepoPathValid() {
        val url = "https://github.com/theapache64/faded"
        val repoName = PathUtils.getRepoPath(url)
        assertEquals("theapache64/faded", repoName)
    }

    @Test
    fun getRepoPathInvalid() {
        val invalidUrl = "theapache64/faded"
        val repoName = PathUtils.getRepoPath(invalidUrl)
        assertEquals(null, repoName)
    }
}