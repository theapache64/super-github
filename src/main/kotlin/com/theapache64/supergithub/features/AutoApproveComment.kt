package com.theapache64.supergithub.features

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window

class AutoApproveComment : BaseFeature {

    companion object {
        private val PR_REVIEW_PAGE_URL_REGEX =
            "https:\\/\\/github\\.com\\/.+?\\/.+?\\/pull\\/\\d+?\\/files#submit-review".toRegex()

        private val APPROVE_MESSAGES = arrayOf(
            "LGTM. Please merge",
            "Wow. Good job mate. LGTM, Feel free to merge",
            "Cool. LGTM. Please merge"
        )
    }

    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        println("URL is -> $url")
        val isPrReviewUrl = PR_REVIEW_PAGE_URL_REGEX.matches(url)
        println("isPrReviewUrl -> $isPrReviewUrl")
        if (isPrReviewUrl) {
            document
                .querySelector("#review-changes-modal > div > div > div > form > div > label > input[type=radio][value=approve]")
                ?.let { element ->
                    element.addEventListener("click", object : EventListener {
                        override fun handleEvent(event: Event) {
                            document.querySelector("#pull_request_review_body")?.let { commentTextArea ->
                                val currentComment = commentTextArea.textContent
                                println("Current comment is '$currentComment'")
                                // Either comment should be empty or any of the approve message should be the content
                                if (currentComment?.isBlank() == true || APPROVE_MESSAGES.contains(currentComment)) {
                                    commentTextArea.textContent = APPROVE_MESSAGES.random()
                                }
                            }
                        }
                    })
                }
        }
    }
}