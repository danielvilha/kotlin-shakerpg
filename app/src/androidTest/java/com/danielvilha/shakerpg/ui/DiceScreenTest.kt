package com.danielvilha.shakerpg.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danielvilha.shakerpg.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiceScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testMainScreenElementsDisplayedSuccess() {
        composeTestRule.onNodeWithTag("dice_selector_bar").assertExists()
        composeTestRule.onNodeWithTag("roll_dice_button").assertExists()
        composeTestRule.onNodeWithTag("dice_arena_tray").assertExists()
        composeTestRule.onNodeWithTag("settings_top_button").assertExists()
        composeTestRule.onNodeWithTag("history_top_button").assertExists()
        composeTestRule.onNodeWithTag("presets_button").assertExists()
    }

    @Test
    fun testDiceRollingInteractionSuccess() {
        composeTestRule.onNodeWithTag("selector_die_d20").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("roll_dice_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("roll_breakdown_card").assertExists()
    }

    @Test
    fun testModifierButtonsSuccess() {
        composeTestRule.onNodeWithTag("mod_plus_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("mod_minus_button").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testOpenPresetsSheetSuccess() {
        composeTestRule.onNodeWithTag("presets_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("presets_modal_sheet").assertExists()
    }

    @Test
    fun testOpenSettingsSheetSuccess() {
        composeTestRule.onNodeWithTag("settings_top_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("settings_modal_sheet").assertExists()
    }

    @Test
    fun testOpenHistorySheetSuccess() {
        composeTestRule.onNodeWithTag("history_top_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("roll_history_modal_sheet").assertExists()
    }
}
