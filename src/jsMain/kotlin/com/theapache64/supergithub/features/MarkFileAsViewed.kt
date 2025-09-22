package com.theapache64.supergithub.features

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.EventListener

class MarkFileAsViewed : BaseFeature {

    companion object {
        private val PR_FILES_PAGE_URL_REGEX =
            "https:\\/\\/github\\.com\\/.+?\\/.+?\\/pull\\/\\d+?\\/files.*".toRegex()
        
        // Multiple selectors to try for the "Viewed" checkbox/button in PR file diff
        private val VIEWED_ELEMENT_SELECTORS = listOf(
            // New button-based selectors
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
                        !isInInputField()) {
                        
                        console.log("'v' key pressed, attempting to mark file as viewed")
                        
                        // Find the currently focused or hovered file section
                        val activeFileElement = findActiveFileViewedElement()

                        if (activeFileElement != null) {
                            if (activeFileElement is HTMLInputElement) {
                                // Handle legacy checkbox
                                val wasChecked = activeFileElement.checked
                                activeFileElement.checked = !activeFileElement.checked

                                // Trigger events
                                val changeEvent = Event("change")
                                activeFileElement.dispatchEvent(changeEvent)
                                val clickEvent = Event("click")
                                activeFileElement.dispatchEvent(clickEvent)

                                val filePath = activeFileElement.getAttribute("data-path") ?: "unknown file"
                                val action = if (wasChecked) "unmarked" else "marked"
                                console.log("Successfully $action file as viewed: $filePath")
                            } else if (activeFileElement is HTMLButtonElement) {
                                // Handle new button format
                                val wasPressed = activeFileElement.getAttribute("aria-pressed") == "true"

                                // Click the button to toggle its state
                                activeFileElement.click()

                                val action = if (wasPressed) "unmarked" else "marked"
                                console.log("Successfully $action file as viewed via button")
                            }

                            // Prevent default action to avoid typing 'v' in text fields
                            event.preventDefault()
                        } else {
                            console.log("No file checkbox or button found to mark as viewed")
                        }
                    }
                }
            })
        } else {
            console.log("Not a PR files page, skipping Mark File as Viewed feature")
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
    
    private fun findActiveFileViewedElement(): Element? {
        // Try different strategies to find the appropriate element

        // Strategy 1: Find element based on current focus or mouse position
        val activeElement = document.activeElement
        var currentElement = activeElement
        
        // Look for the nearest file diff container
        while (currentElement != null) {
            // Check if this is a file diff container (common GitHub class patterns)
            val classList = currentElement.classList
            if (classList.contains("file") || 
                classList.contains("file-header") || 
                currentElement.getAttribute("data-tagsearch-path") != null ||
                currentElement.getAttribute("data-path") != null) {
                
                // Found a file container, now find its viewed element
                val filePath = currentElement.getAttribute("data-path") ?:
                              currentElement.getAttribute("data-tagsearch-path")
                
                if (filePath != null) {
                    // Try to find element with specific file path
                    for (selector in VIEWED_ELEMENT_SELECTORS) {
                        val element = currentElement.querySelector(selector)
                        if (element != null) {
                            return element
                        }
                    }
                }
                break
            }
            currentElement = currentElement.parentElement
        }
        
        // Strategy 2: Look for buttons with "Viewed" text that are not pressed
        val viewedButtons = document.querySelectorAll("button")
        for (i in 0 until viewedButtons.length) {
            val button = viewedButtons.item(i) as? HTMLButtonElement
            if (button != null) {
                val textContent = button.textContent?.lowercase()
                if (textContent?.contains("viewed") == true &&
                    button.getAttribute("aria-pressed") != "true") {
                    return button
                }
            }
        }

        // Strategy 3: Find the first unchecked checkbox (legacy support)
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
        
        // Strategy 4: Return the first button with "Viewed" text found (if any)
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