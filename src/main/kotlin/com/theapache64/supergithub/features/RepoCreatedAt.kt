package com.theapache64.supergithub.features

import com.theapache64.supergithub.data.repositories.GitHubRepo
import com.theapache64.supergithub.utils.PathUtils
import com.theapache64.supergithub.utils.TimeUtils
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
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

                    val div =
                        document.querySelector("#repository-container-header > div.d-flex.mb-3.px-3.px-md-4.px-lg-5 > div")
                    //document.querySelector("body > div.application-main > div > main > div.pagehead.repohead.hx_repohead.readability-menu.bg-gray-light.pb-0.pt-0.pt-lg-3 > div.d-flex.mb-4.p-responsive.d-none.d-lg-flex > div > h1")

                    val hasBirthDate = div?.querySelector("span#sg_created_at") != null
                    if (div != null && !hasBirthDate) {

                        println("Added created date")

                        div.innerHTML += """
                        $timeEmoji
                        <span id="sg_created_at" title="$repoCreatedDate" style="font-size: 18px; color:#5a5a5a"> 
                        Born $timesAgo
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

        val time: Pair<String, String> = when (repoCreatedAt.getHours()) {
            in 0..5 -> Pair("Midnight", "1f319") // "🌙"
            in 5..10 -> Pair("Morning", "1f31e") // "🌞"
            in 10..15 -> Pair("Noon", "2600") // "☀️"
            in 15..19 -> Pair("Evening", "1f325") // "🌥"
            else -> Pair("Night", "1f313") // "🌓"
        }

        return """
            <img id="sg_emoji" title="${time.first}" style="margin-top: 6px;" class="emoji" height="17" width="17" src="https://github.githubassets.com/images/icons/emoji/unicode/${time.second}.png">
        """.trimIndent()
    }

}