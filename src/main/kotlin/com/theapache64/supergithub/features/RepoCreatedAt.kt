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
        println("Finding repo created date")


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
                        document.querySelector("#js-repo-pjax-container > div.pagehead.repohead.hx_repohead.readability-menu.bg-gray-light.pb-0 > div > div > h1")

                    if (h1 != null) {

                        println("Added created date")

                        h1.innerHTML += """
                        $timeEmoji
                        <span id="sg_created_at" title="$repoCreatedDate" style="font-size: 14px;margin-bottom: 4px;"> 
                        Created $timesAgo
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
        val emojiCode = when (repoCreatedAt.getHours()) {
            in 0..5 -> "1f319" // "🌙"
            in 5..10 -> "1f31e" // "🌞"
            in 10..15 -> "2600" // "☀️"
            in 15..19 -> "1f325" // "🌥"
            else -> "1f313" // "🌓"
        }

        return """
            <img id="sg_emoji" style="margin-bottom: 10px; margin-right: 6px;" class="emoji" alt="first_quarter_moon" height="20" width="20" src="https://github.githubassets.com/images/icons/emoji/unicode/$emojiCode.png">
        """.trimIndent()
    }

}