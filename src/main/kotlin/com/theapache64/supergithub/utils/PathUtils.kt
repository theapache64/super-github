package com.theapache64.supergithub.utils

object PathUtils {

    private val PROFILE_REGEX = "^https:\\/\\/github\\.com\\/[\\w-]+\\/?\$".toRegex()

    fun getRepoPath(fullUrl: String): String? {
        val slashSplit = fullUrl.split("/")
        if (slashSplit.size >= 5) {
            return "${slashSplit[3]}/${slashSplit[4]}".trim()
        }
        return null
    }

    fun isProfilePage(url: String): Boolean {
        return url.matches(PROFILE_REGEX)
    }

    fun getUsername(fullUrl: String): String? {
        val slashSplit = fullUrl.split("/")
        if (slashSplit.size >= 4) {
            return slashSplit[3].trim()
        }
        return null
    }
}