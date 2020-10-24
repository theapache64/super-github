package com.theapache64.supergithub.features

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window

class PRApproveAutoComment : BaseFeature {

    companion object {
        private val PR_REVIEW_PAGE_URL_REGEX =
            "https:\\/\\/github\\.com\\/.+?\\/.+?\\/pull\\/\\d+?\\/files#submit-review".toRegex()

        private val approveActions = mapOf(
            "approve" to arrayOf(
                "LGTM 👌. Feel free to merge",
                "Wow. Good job mate 👍. Feel free to merge",
                "Cool. LGTM. Please merge 🚀"
            )
        )

        private const val RADIO_SELECTOR =
            "#review-changes-modal > div > div > div > form > div > label > input[type=radio]"
    }

    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        val isPrReviewUrl = PR_REVIEW_PAGE_URL_REGEX.matches(url)

        if (isPrReviewUrl) {

            for (action in approveActions.keys) {

                val messages = approveActions[action] ?: error("TSH: Couldn't find key $action")

                document
                    .querySelector("$RADIO_SELECTOR[value=$action]")
                    ?.let { element ->
                        element.addEventListener("click", object : EventListener {
                            override fun handleEvent(event: Event) {
                                document.querySelector("#pull_request_review_body")?.let { commentTextArea ->
                                    val currentComment = commentTextArea.textContent
                                    println("Current comment is '$currentComment'")
                                    // Either comment should be empty or any of the approve message should be the content
                                    if (currentComment?.isBlank() == true || messages.contains(currentComment)) {
                                        commentTextArea.textContent = messages.random()
                                    }
                                }
                            }
                        })
                    }
            }


        }
    }
}