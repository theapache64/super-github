package com.theapache64.supergithub

import com.theapache64.supergithub.data.repositories.GitHubRepo
import com.theapache64.supergithub.features.BaseFeature
import com.theapache64.supergithub.features.RepoCreatedAt
import com.theapache64.supergithub.utils.PathUtils
import com.theapache64.supergithub.utils.TimeUtils
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.set
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.dom.appendText
import kotlin.js.Date

suspend fun main() {

    val features = listOf<BaseFeature>(
        RepoCreatedAt()
    )

    for(feature in features){
        feature.onGitHubPageLoaded()
    }
}