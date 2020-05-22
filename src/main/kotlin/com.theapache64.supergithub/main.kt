package com.theapache64.supergithub

import com.theapache64.supergithub.features.BaseFeature
import com.theapache64.supergithub.features.ProfileSummary
import com.theapache64.supergithub.features.RepoCreatedAt

suspend fun main() {

    val features = listOf<BaseFeature>(
        RepoCreatedAt(),
        ProfileSummary()
    )

    for (feature in features) {
        feature.onGitHubPageLoaded()
    }
}