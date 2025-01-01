package com.snappbox.newslist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.snappbox.ui.theme.SnappNewsTheme
import org.junit.Rule
import org.junit.Test

class NewsNotificationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNewsNotificationVisibility() {
        // Set up an initial count state and change it over time
        var count by mutableStateOf(0)

        // Run the composable in the test environment
        composeTestRule.setContent {
            SnappNewsTheme {
                NewsNotification(
                    count = count,
                    onClick = {}
                )
            }
        }

        // Initially, count is 0, so the AnimatedVisibility should not show the notification
        composeTestRule.onNodeWithText("New Articles (1)").assertDoesNotExist()

        // Update the count to make the notification visible
        composeTestRule.runOnIdle {
            count = 1
        }

        // Advance the clock to allow the animation to start and complete
        composeTestRule.mainClock.advanceTimeBy(500)

        // Now, the notification should be visible
        composeTestRule.onNodeWithText("New Articles (1)").assertIsDisplayed()

        // Change the count back to 0 to hide the notification
        composeTestRule.runOnIdle {
            count = 0
        }

        // Advance the clock again to allow the exit animation to complete
        composeTestRule.mainClock.advanceTimeBy(500)

        // The notification should no longer be visible
        composeTestRule.onNodeWithText("New Articles (1)").assertDoesNotExist()
    }
}
