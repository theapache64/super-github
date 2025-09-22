package com.theapache64.supergithub.features

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.EventListener

class MarkFileAsViewed : BaseFeature {

    companion object {
        private val PR_FILES_PAGE_URL_REGEX =
            "https:\\/\\/github\\.com\\/.+?\\/.+?\\/pull\\/\\d+?\\/files.*".toRegex()
        
        // Multiple selectors to try for the "Viewed" checkbox in PR file diff
        private val VIEWED_CHECKBOX_SELECTORS = listOf(
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
                        val activeFileCheckbox = findActiveFileViewedCheckbox()
                        
                        if (activeFileCheckbox != null) {
                            val wasChecked = activeFileCheckbox.checked
                            // Toggle the checkbox
                            activeFileCheckbox.checked = !activeFileCheckbox.checked
                            
                            // Trigger the change event to ensure GitHub's JavaScript handlers are called
                            val changeEvent = Event("change")
                            activeFileCheckbox.dispatchEvent(changeEvent)
                            
                            // Also trigger click event for better compatibility
                            val clickEvent = Event("click")
                            activeFileCheckbox.dispatchEvent(clickEvent)
                            
                            val filePath = activeFileCheckbox.getAttribute("data-path") ?: "unknown file"
                            val action = if (wasChecked) "unmarked" else "marked"
                            console.log("Successfully $action file as viewed: $filePath")
                            
                            // Prevent default action to avoid typing 'v' in text fields
                            event.preventDefault()
                        } else {
                            console.log("No file checkbox found to mark as viewed")
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
    
    private fun findActiveFileViewedCheckbox(): HTMLInputElement? {
        // Try different strategies to find the appropriate checkbox
        
        // Strategy 1: Find checkbox based on current focus or mouse position
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
                
                // Found a file container, now find its viewed checkbox
                val filePath = currentElement.getAttribute("data-path") ?: 
                              currentElement.getAttribute("data-tagsearch-path")
                
                if (filePath != null) {
                    // Try to find checkbox with specific file path
                    for (selector in VIEWED_CHECKBOX_SELECTORS) {
                        val checkbox = currentElement.querySelector(selector) as? HTMLInputElement
                        if (checkbox != null) {
                            return checkbox
                        }
                    }
                }
                break
            }
            currentElement = currentElement.parentElement
        }
        
        // Strategy 2: Find the first unchecked checkbox
        for (selector in VIEWED_CHECKBOX_SELECTORS) {
            val allCheckboxes = document.querySelectorAll(selector)
            for (i in 0 until allCheckboxes.length) {
                val checkbox = allCheckboxes.item(i) as? HTMLInputElement
                if (checkbox != null && !checkbox.checked) {
                    return checkbox
                }
            }
        }
        
        // Strategy 3: Return the first checkbox found (if any)
        for (selector in VIEWED_CHECKBOX_SELECTORS) {
            val allCheckboxes = document.querySelectorAll(selector)
            if (allCheckboxes.length > 0) {
                return allCheckboxes.item(0) as? HTMLInputElement
            }
        }
        
        return null
    }
}