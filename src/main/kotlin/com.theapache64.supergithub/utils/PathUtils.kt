package com.theapache64.supergithub.utils

object PathUtils {
    fun getRepoPath(fulUrl: String): String? {
        val slashSplit = fulUrl.split("/")
        if (slashSplit.size >= 5) {
            return "${slashSplit[3]}/${slashSplit[4]}"
        }
        return null
    }
}