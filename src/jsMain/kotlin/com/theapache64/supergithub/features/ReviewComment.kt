package com.theapache64.supergithub.features

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

class ReviewComment : BaseFeature {

    companion object {
        private val PR_REVIEW_PAGE_URL_REGEX =
            "https:\\/\\/github\\.com\\/.+?\\/.+?\\/pull\\/\\d+?\\/files.*".toRegex()

        private val reviewActions = mapOf(
            "approve" to listOf(
                "LGTM 👌. Feel free to merge",
                "Wow. Good job mate 👍. Feel free to merge",
                "Cool. LGTM. Please merge 🚀",
                "Hell of a job 👍. LGTM. Please merge"
            ),
            "comment" to listOf(
                "I am comment value 1",
                "I am comment value 2",
            )
        )

        private val allMessages = reviewActions.values.flatten()

        private const val RADIO_SELECTOR =
            "#review-changes-modal > div > div > div > form > div > label > input[type=radio]"
    }

    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        val isPrReviewUrl = PR_REVIEW_PAGE_URL_REGEX.matches(url)

        if (isPrReviewUrl) {

            for (action in reviewActions.keys) {

                val messages = reviewActions[action] ?: error("TSH: Couldn't find key $action")

                document
                    .querySelector("$RADIO_SELECTOR[value=$action]")
                    ?.let { element ->
                        element.addEventListener("click", object : EventListener {
                            override fun handleEvent(event: Event) {
                                document.querySelector("#pull_request_review_body")?.let { commentTextArea ->
                                    val currentComment = (commentTextArea as HTMLTextAreaElement).value
                                    println("Current comment is '$currentComment'")
                                    // Either comment should be empty or any of the messages should be the content
                                    if (currentComment.isBlank() || allMessages.contains(currentComment)) {
                                        val newMessage = messages.random()
                                        console.log("Changing review message to $newMessage")
                                        commentTextArea.value = newMessage
                                    }
                                }
                            }
                        })
                    } ?: console.error("Uhh ho! failed to find the element")
            }


        }
    }
}