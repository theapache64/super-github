package com.theapache64.supergithub.features

import com.theapache64.supergithub.utils.PathUtils
import kotlin.browser.window

class ProfileSummary : BaseFeature {
    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        if (PathUtils.isProfilePage(url)) {

        } else {
            println("$url is not profile page")
        }
    }
}