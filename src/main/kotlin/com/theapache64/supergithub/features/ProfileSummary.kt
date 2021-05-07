package com.theapache64.supergithub.features

import com.theapache64.supergithub.utils.PathUtils
import kotlinx.browser.document
import kotlinx.browser.window

class ProfileSummary : BaseFeature {
    companion object{
        private const val PROFILE_NAME_SELECTOR = "#js-pjax-container > div.container-xl.px-3.px-md-4.px-lg-5 > div > div.flex-shrink-0.col-12.col-md-3.mb-4.mb-md-0 > div > div.clearfix.d-flex.d-md-block.flex-items-center.mb-4.mb-md-0 > div.vcard-names-container.float-left.col-12.py-3.js-sticky.js-user-profile-sticky-fields > h1 > span.p-name.vcard-fullname.d-block.overflow-hidden"
        private const val ORG_SELECTOR = "#js-pjax-container > div > div > div.border-bottom.color-border-secondary.mb-3.pb-md-3 > div > form > div"
    }
    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        if (PathUtils.isProfilePage(url)) {

            val profileNameElement = document.querySelector(PROFILE_NAME_SELECTOR)

            val username = PathUtils.getUsername(url)
            if (username == null) {
                println("Couldn't find username from $url")
                return
            }

            println("Username is $username")

            if (profileNameElement != null) {
                println("Okay")
                val summaryButton = getSummaryButton(username)
                profileNameElement.innerHTML += summaryButton

            } else {

                // Maybe organization
                val element =
                    document.querySelector(ORG_SELECTOR)
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