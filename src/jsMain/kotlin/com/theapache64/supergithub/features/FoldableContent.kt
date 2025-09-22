package com.theapache64.supergithub.features

import com.theapache64.supergithub.utils.foldableSvg
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

class FoldableContent : BaseFeature {
    companion object {
        private val COMMENTABLE_PAGE_URL_REGEX =
            "https:\\/\\/github\\.com\\/.+?\\/.+?\\/(issues|pull|issues)\\/(new|\\d+.*)".toRegex()

        private const val TARGET_CONTAINER_EXISTING_ISSUE_SELECTOR =
            "#issuecomment-new > div.border-0.border-md.timeline-comment.timeline-comment--caret > form > fieldset > tab-container > div.comment-form-head.tabnav.d-flex.flex-justify-between.mb-2.p-0.tabnav--responsive.flex-column.border-bottom-0.mb-0.mb-lg-2.flex-items-stretch.border-lg-bottom.color-border-primary.flex-lg-items-center.flex-lg-row > markdown-toolbar > div.flex-nowrap.d-none.d-md-inline-block.mr-3"

        private const val TARGET_CONTAINER_NEW_ISSUE_SELECTOR =
            "#new_issue > div > div > div.flex-shrink-0.col-12.col-md-9.mb-4.mb-md-0 > div > div.timeline-comment.color-bg-canvas.hx_comment-box--tip > div > tab-container > div.comment-form-head.tabnav.d-flex.flex-justify-between.mb-2.p-0.tabnav--responsive.flex-column.border-bottom-0.mb-0.mb-lg-2.flex-items-stretch.border-lg-bottom.color-border-primary.flex-lg-items-center.flex-lg-row > markdown-toolbar > div.flex-nowrap.d-none.d-md-inline-block.mr-3"
        private val MD_FOLDABLE by lazy {
            """
            <md-foldable tabindex="-1" class="toolbar-item tooltipped tooltipped-sw mx-1" aria-label="Add foldable content" role="button">
            $foldableSvg
            </md-foldable>
        """
        }

        private val FOLDABLE_TEMPLATE = """
            <details>
            <summary>SUMMARY_GOES_HERE</summary>
            
            ```
            PASTE_CONTENT_HERE
            ```
            </details>
        """.trimIndent()

        private const val TEXTAREA_EXISTING_SELECTOR = "#new_comment_field"
        private const val TEXTAREA_NEW_SELECTOR = "#issue_body"


    }

    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        val isCommentablePage = COMMENTABLE_PAGE_URL_REGEX.matches(url)


        println("Is commentable ('$url') : $isCommentablePage")

        if (isCommentablePage) {
            document
                .querySelector(TARGET_CONTAINER_EXISTING_ISSUE_SELECTOR)
                .let { container ->
                    container ?: document.querySelector(TARGET_CONTAINER_NEW_ISSUE_SELECTOR)
                }
                ?.let { container ->
                    container.innerHTML = MD_FOLDABLE + container.innerHTML

                    // Add click listener
                    document.querySelector("md-foldable")
                        ?.addEventListener(
                            "click",
                            object : EventListener {
                                override fun handleEvent(event: Event) {
                                    // add content
                                    (document.querySelector(TEXTAREA_EXISTING_SELECTOR) ?: document.querySelector(TEXTAREA_NEW_SELECTOR))
                                        ?.let { textArea ->
                                            textArea as HTMLTextAreaElement
                                            val startPos = textArea.selectionStart
                                            val endPos = textArea.selectionEnd
                                            require(startPos != null) { "startPos is null" }
                                            require(endPos != null) { "startPos is null" }
                                            val currentValue = textArea.value
                                            textArea.value = currentValue.substring(
                                                0,
                                                startPos
                                            ) + FOLDABLE_TEMPLATE + currentValue.substring(endPos, currentValue.length)
                                        }
                                }
                            }
                        ) ?: println("Couldn't find foldable")
                }
        }
    }
}