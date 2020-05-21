package com.theapache64.supergithub.features

import com.theapache64.supergithub.data.repositories.GitHubRepo
import com.theapache64.supergithub.utils.PathUtils
import com.theapache64.supergithub.utils.TimeUtils
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.js.Date

class RepoCreatedAt : BaseFeature {

    override suspend fun onGitHubPageLoaded() {

        val repoPath = PathUtils.getRepoPath(window.location.href)
        if (repoPath != null) {
            val createdAtKey = "sg_$repoPath"
            val repoCreatedAt = localStorage.getItem(createdAtKey) ?: GitHubRepo.getRepo(repoPath).created_at

            if (repoCreatedAt != null) {

                localStorage.setItem(createdAtKey, repoCreatedAt)

                val repoCreatedDate = Date(repoCreatedAt)
                val timeEmoji = getTimeEmoji(repoCreatedDate)

                val timesAgo = TimeUtils.getRelativeTime(Date(), repoCreatedDate)
                if (timesAgo != null) {

                    val h1 =
                        document.querySelector("#js-repo-pjax-container > div.pagehead.repohead.hx_repohead.readability-menu.bg-gray-light.pb-0.pt-3 > div > div > h1")

                    if (h1 != null) {

                        h1.innerHTML += """
                        <span id="sg_created_at" title="$repoCreatedDate" style="font-size: 16px;margin-bottom: 4px;"> 
                        $timeEmoji Created $timesAgo
                        </span>
                    """.trimIndent()

                    } else {
                        println("Selector algorithm failed")
                    }
                } else {
                    println("Invalid creation date $repoCreatedDate")
                }
            } else {
                println("Invalid rep $repoPath")
            }
        } else {
            println("It's not a repo")
        }
    }

    private fun getTimeEmoji(repoCreatedAt: Date): String {
        return when (repoCreatedAt.getHours()) {
            in 0..5 -> "🌙"
            in 5..10 -> "🌞"
            in 10..15 -> "☀️"
            in 15..19 -> "🌥"
            else -> "🌓"
        }
    }

}