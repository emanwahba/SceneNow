package com.emanwahba.scenenow

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() = hiltRule.inject()

    @Test
    fun tappingMovieOpensDetailAndBackReturnsToList() {
        composeRule.onNodeWithText("Trending this week").assertIsDisplayed()
        composeRule.onNodeWithText("Quiet Harbor").assertIsDisplayed()

        composeRule.onNodeWithText("Quiet Harbor").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTextCount("Detailed overview for movie 2.") > 0
        }
        composeRule.onNodeWithText("Detailed overview for movie 2.").assertIsDisplayed()
        composeRule.onNodeWithText("Trending this week").assertDoesNotExist()

        composeRule.onNodeWithContentDescription("Back").performClick()

        composeRule.onNodeWithText("Trending this week").assertIsDisplayed()
        composeRule.onNodeWithText("Galactic Heist").assertIsDisplayed()
    }

    private fun androidx.compose.ui.test.junit4.ComposeTestRule.onAllNodesWithTextCount(text: String) =
        onAllNodesWithText(text).fetchSemanticsNodes().size
}
