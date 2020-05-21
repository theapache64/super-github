package com.theapache64.supergithub.features

interface BaseFeature {
    suspend fun onGitHubPageLoaded()
}