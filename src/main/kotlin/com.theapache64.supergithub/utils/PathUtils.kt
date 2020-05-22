package com.theapache64.supergithub.utils

object PathUtils {

    private val PROFILE_REGEX = "^https:\\/\\/github\\.com\\/\\w+\\/?\$".toRegex()

    fun getRepoPath(fulUrl: String): String? {
        val slashSplit = fulUrl.split("/")
        if (slashSplit.size >= 5) {
            return "${slashSplit[3]}/${slashSplit[4]}"
        }
        return null
    }

    fun isProfilePage(url: String): Boolean {
        return url.matches(PROFILE_REGEX)
    }
}