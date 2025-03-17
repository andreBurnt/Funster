package co.funster.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Sample Espresso test to launch the app and verify that event list Composable
 * is displayed.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun eventListIsDisplayedAfterLoading() {
        // Wait for event list to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNodeWithTag("event_list").isDisplayed()
        }

        // Assert event list is visible
        composeTestRule.onNodeWithTag("event_list").assertIsDisplayed()
    }
}
