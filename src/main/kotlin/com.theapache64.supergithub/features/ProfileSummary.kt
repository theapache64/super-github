package com.theapache64.supergithub.features

import com.theapache64.supergithub.utils.PathUtils
import kotlin.browser.document
import kotlin.browser.window

class ProfileSummary : BaseFeature {
    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        if (PathUtils.isProfilePage(url)) {

            val profileNameElement =
                document.querySelector("#js-pjax-container > div > div.h-card.col-lg-3.col-md-4.col-12.float-md-left.pr-md-3.pr-xl-6 > div:nth-child(2) > div.vcard-names-container.float-left.col-9.col-md-12.pt-1.pt-md-3.pb-1.pb-md-3.js-sticky.js-user-profile-sticky-fields > h1 > span.p-name.vcard-fullname.d-block.overflow-hidden")

            val username = PathUtils.getUsername(url)
            if (username == null) {
                println("Couldn't find username from $url")
                return
            }

            if (profileNameElement != null) {

                val summaryButton = getSummaryButton(username)
                profileNameElement.innerHTML += summaryButton

            } else {

                // Maybe organization
                val element =
                    document.querySelector("#js-pjax-container > div > div > div.border-bottom.border-gray-light.mb-3.pb-md-3 > div > form > div")
                if (element != null) {
                    val html = getSummaryButton(username, "margin-top: 4px; margin-right:10px;")
                    element.innerHTML = "$html ${element.innerHTML}"
                } else {
                    println("Query selector failed")
                }
            }

        } else {
            println("$url is not profile page")
        }
    }

    private fun getSummaryButton(username: String, imageStyle: String = ""): String {
        return """
                    <a title="View Profile Summary" target="_blank" id="sg_a_summary_button"  style="float: right;" href="https://profile-summary-for-github.com/user/$username">
                    <img style="width: 27px; height: 27px;$imageStyle" src="https://raw.githubusercontent.com/theapache64/super-github/master/assets/summary.svg" id="sg_img_summary_button">
                    </a>
                    """.trimIndent()
    }
}