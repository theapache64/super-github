package com.theapache64.supergithub

import com.theapache64.supergithub.data.repositories.GitHubRepo
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
    println("Current window URL is ${window.location}")
    val repoPath = PathUtils.getRepoPath(window.location.href)
    if (repoPath != null) {

        val repoCreatedAt = localStorage.getItem(repoPath) ?: GitHubRepo.getRepo(repoPath).created_at

        if (repoCreatedAt != null) {

            localStorage.setItem(repoPath, repoCreatedAt)

            val repoCreatedDate = Date(repoCreatedAt)
            val timesAgo = TimeUtils.getRelativeTime(Date(), repoCreatedDate)
            if (timesAgo != null) {
                val h1 =
                    document.querySelector("#js-repo-pjax-container > div.pagehead.repohead.hx_repohead.readability-menu.bg-gray-light.pb-0.pt-3 > div > div > h1")

                if (h1 != null) {

                    h1.innerHTML += """
                        <span id="sg_created_at" title="$repoCreatedDate" style="font-size: 16px;margin-bottom: 4px;"> 
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