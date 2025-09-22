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
        
        // Selector for the "Viewed" checkbox in PR file diff
        private const val VIEWED_CHECKBOX_SELECTOR = "input[type='checkbox'][data-path]"
    }

    override suspend fun onGitHubPageLoaded() {
        val url = window.location.href
        val isPrFilesUrl = PR_FILES_PAGE_URL_REGEX.matches(url)

        if (isPrFilesUrl) {
            console.log("Setting up Mark File as Viewed keyboard shortcut")
            
            // Add keyboard event listener for the 'v' key
            document.addEventListener("keydown", object : EventListener {
                override fun handleEvent(event: Event) {
                    val keyEvent = event as KeyboardEvent
                    
                    // Check if 'v' key is pressed (without any modifiers)
                    if (keyEvent.key == "v" && !keyEvent.ctrlKey && !keyEvent.altKey && !keyEvent.shiftKey && !keyEvent.metaKey) {
                        // Find the currently focused or hovered file section
                        val activeFileCheckbox = findActiveFileViewedCheckbox()
                        
                        if (activeFileCheckbox != null) {
                            // Toggle the checkbox
                            activeFileCheckbox.checked = !activeFileCheckbox.checked
                            
                            // Trigger the change event to ensure GitHub's JavaScript handlers are called
                            val changeEvent = Event("change")
                            activeFileCheckbox.dispatchEvent(changeEvent)
                            
                            console.log("Toggled viewed status for file: ${activeFileCheckbox.getAttribute("data-path")}")
                            
                            // Prevent default action to avoid typing 'v' in text fields
                            event.preventDefault()
                        } else {
                            console.log("No active file found to mark as viewed")
                        }
                    }
                }
            })
        }
    }
    
    private fun findActiveFileViewedCheckbox(): HTMLInputElement? {
        // Try to find the checkbox for the currently focused/hovered file
        // First, try to find if we're inside a specific file diff section
        val activeElement = document.activeElement
        
        // Look for the nearest file diff container
        var currentElement = activeElement
        while (currentElement != null) {
            // Check if this is a file diff container
            if (currentElement.getAttribute("data-tagsearch-path") != null) {
                // Found the file container, now find its viewed checkbox
                val filePath = currentElement.getAttribute("data-tagsearch-path")
                val checkbox = document.querySelector("input[type='checkbox'][data-path='$filePath']") as? HTMLInputElement
                if (checkbox != null) {
                    return checkbox
                }
            }
            currentElement = currentElement.parentElement
        }
        
        // If no specific file is focused, try to find the first unchecked "Viewed" checkbox
        val allCheckboxes = document.querySelectorAll(VIEWED_CHECKBOX_SELECTOR)
        for (i in 0 until allCheckboxes.length) {
            val checkbox = allCheckboxes.item(i) as HTMLInputElement
            if (!checkbox.checked) {
                return checkbox
            }
        }
        
        // If all files are marked as viewed, return the first checkbox
        if (allCheckboxes.length > 0) {
            return allCheckboxes.item(0) as HTMLInputElement
        }
        
        return null
    }
}