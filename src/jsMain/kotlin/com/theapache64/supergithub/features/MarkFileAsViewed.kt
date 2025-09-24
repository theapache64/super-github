package com.theapache64.supergithub.features

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent

class MarkFileAsViewed : BaseFeature {

    companion object {
        private val PR_FILES_PAGE_URL_REGEX =
            "https://github\\.com/.+?/.+?/pull/\\d+?/files.*".toRegex()

        // Multiple selectors to try for the "Viewed" checkbox/button in PR file diff
        private val VIEWED_ELEMENT_SELECTORS = listOf(
            // New button-based selectors for current GitHub structure
            "button[aria-pressed]", // Buttons with aria-pressed attribute
            "button[class*='MarkAsViewedButton']", // Buttons with MarkAsViewedButton class
            "button:has([data-component='text']:contains('Viewed'))", // Buttons containing "Viewed" text
            "button[aria-describedby*='loading-announcement']:has([data-component='text']:contains('Viewed'))",
            "button:has([data-component='text']:contains('Viewed'))",
            "button[data-component='buttonContent']:has(span:contains('Viewed'))",
            "button:has(span:contains('Viewed'))",
            // Legacy checkbox selectors
            "input[type='checkbox'][name='viewed']",
            "input[type='checkbox'][data-path]",
            "input[type='checkbox'].js-reviewed-checkbox",
            ".js-reviewed-checkbox input[type='checkbox']",
            "[data-testid='file-viewed-checkbox']"
        )
    }

    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        val isPrFilesUrl = PR_FILES_PAGE_URL_REGEX.matches(url)

        if (isPrFilesUrl) {
            console.log("Setting up Mark File as Viewed keyboard shortcut on: $url")

            // Add keyboard event listener for the 'v' key
            document.addEventListener("keydown", object : EventListener {
                override fun handleEvent(event: Event) {
                    val keyEvent = event as KeyboardEvent

                    // Check if 'v' key is pressed (without any modifiers)
                    // Also ensure we're not in an input field
                    if (keyEvent.key == "v" &&
                        !keyEvent.ctrlKey &&
                        !keyEvent.altKey &&
                        !keyEvent.shiftKey &&
                        !keyEvent.metaKey &&
                        !isInInputField()
                    ) {

                        console.log("'v' key pressed, attempting to mark file as viewed")

                        val uncheckedViewedButton = findFirstUncheckedViewedButton()
                        if (uncheckedViewedButton != null) {
                            uncheckedViewedButton.click()
                            console.log("Marked file as viewed")
                        } else {
                            console.log("No unchecked 'Viewed' button found")
                        }
                    }
                }
            })
        } else {
            console.log("Not a PR files page, skipping Mark File as Viewed feature: $url")
        }
    }

    private fun isInInputField(): Boolean {
        val activeElement = document.activeElement
        return when (activeElement?.tagName?.lowercase()) {
            "input", "textarea", "select" -> true
            else -> {
                // Check if we're in a contenteditable element
                activeElement?.getAttribute("contenteditable") == "true"
            }
        }
    }

    private fun findFirstUncheckedViewedButton(): HTMLButtonElement? {
        // first button[aria-pressed="false"] in the body element
        val body = document.body ?: return null
        val buttons = body.querySelectorAll("button[aria-pressed='false']")
        for (i in 0 until buttons.length) {
            val button = buttons.item(i) as? HTMLButtonElement
            if (button != null) {
                val textContent = button.textContent?.lowercase()
                if (textContent?.contains("viewed") == true) {
                    return button
                }
            }
        }
        return null
    }


    private fun findFirstViewedElement(): Element? {
        // Strategy 1: Look for buttons with "Viewed" text that are not pressed
        val viewedButtons = document.querySelectorAll("button")
        for (i in 0 until viewedButtons.length) {
            val button = viewedButtons.item(i) as? HTMLButtonElement
            if (button != null) {
                val textContent = button.textContent?.lowercase()
                if (textContent?.contains("viewed") == true &&
                    button.getAttribute("aria-pressed") != "true"
                ) {
                    return button
                }
            }
        }

        // Strategy 2: Find the first unchecked checkbox (legacy support)
        for (selector in VIEWED_ELEMENT_SELECTORS) {
            if (selector.contains("input[type='checkbox']")) {
                val allCheckboxes = document.querySelectorAll(selector)
                for (i in 0 until allCheckboxes.length) {
                    val checkbox = allCheckboxes.item(i) as? HTMLInputElement
                    if (checkbox != null && !checkbox.checked) {
                        return checkbox
                    }
                }
            }
        }

        // Strategy 3: Return the first button with "Viewed" text found (if any)
        val allViewedButtons = document.querySelectorAll("button")
        for (i in 0 until allViewedButtons.length) {
            val button = allViewedButtons.item(i) as? HTMLButtonElement
            if (button != null && button.textContent?.lowercase()?.contains("viewed") == true) {
                return button
            }
        }

        return null
    }
}