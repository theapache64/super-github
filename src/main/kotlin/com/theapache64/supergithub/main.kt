package com.theapache64.supergithub

import com.theapache64.supergithub.features.BaseFeature
import com.theapache64.supergithub.features.ProfileSummary
import com.theapache64.supergithub.features.RepoCreatedAt
import com.theapache64.supergithub.utils.StringUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLSpanElement
import kotlin.browser.document
import kotlin.browser.window


var prevUrl = window.location.toString()

suspend fun main() {

    runSuperGithub()

    watchForUrlChange {
        println("URL changed, runSuperGithub")
        runSuperGithub()
    }
}

suspend fun runSuperGithub() {

    val features = listOf(
        RepoCreatedAt(),
        ProfileSummary()
    )

    for (feature in features) {
        println("Executing feature...")
        feature.onGitHubPageLoaded()
    }
}

private fun watchForUrlChange(callback: suspend () -> Unit) {
    window.setInterval({
        val newUrl = window.location.toString()
        if (newUrl != prevUrl) {
            prevUrl = newUrl
            println("URL changed!!")

            // Waiting for loading to be finished
            val progressBar =
                document.querySelector("body > div.position-relative.js-header-wrapper > span > span") as HTMLSpanElement

            GlobalScope.launch {
                var curProgress = 0
                while (curProgress < 100) {
                    val progress = StringUtils.parseInt(progressBar.style.width)
                    if (progress != null) {
                        curProgress = progress
                    } else {
                        break
                    }
                    delay(200)
                }
                println("Hehe, progress is $curProgress")
                if (curProgress >= 100) {
                    callback()
                } else {
                    println("Page not loaded fully")
                }
            }
        }
    }, 500)
}