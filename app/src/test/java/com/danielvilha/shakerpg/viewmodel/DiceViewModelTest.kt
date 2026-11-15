package com.danielvilha.shakerpg.viewmodel

import com.danielvilha.shakerpg.model.RollMode
import com.danielvilha.shakerpg.model.RpgDieType
import com.danielvilha.shakerpg.sensor.ShakeSensitivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAddAndRemoveDiceSuccess() {
        val viewModel = DiceViewModel()
        viewModel.clearPool()
        assertEquals(0, viewModel.uiState.value.totalDiceCount)

        viewModel.addDie(RpgDieType.D20)
        assertEquals(1, viewModel.uiState.value.poolCounts[RpgDieType.D20])
        assertEquals("1d20", viewModel.uiState.value.formulaString)

        viewModel.addDie(RpgDieType.D20)
        assertEquals(2, viewModel.uiState.value.poolCounts[RpgDieType.D20])
        assertEquals("2d20", viewModel.uiState.value.formulaString)

        viewModel.removeDie(RpgDieType.D20)
        assertEquals(1, viewModel.uiState.value.poolCounts[RpgDieType.D20])
        assertEquals("1d20", viewModel.uiState.value.formulaString)

        viewModel.removeDie(RpgDieType.D20)
        assertNull(viewModel.uiState.value.poolCounts[RpgDieType.D20])
        assertEquals("0 dice", viewModel.uiState.value.formulaString)
    }

    @Test
    fun testMaxDiceLimitFailureEdgeCase() {
        val viewModel = DiceViewModel()
        viewModel.clearPool()

        // Try adding 25 dice (max allowed is 20)
        repeat(25) {
            viewModel.addDie(RpgDieType.D6)
        }

        assertEquals(20, viewModel.uiState.value.totalDiceCount)
    }

    @Test
    fun testModifierClampingFailureEdgeCase() {
        val viewModel = DiceViewModel()

        viewModel.setModifier(150)
        assertEquals(99, viewModel.uiState.value.modifier)

        viewModel.setModifier(-100)
        assertEquals(-50, viewModel.uiState.value.modifier)

        viewModel.incrementModifier() // -49
        viewModel.decrementModifier() // -50
        viewModel.decrementModifier() // should stay at -50
        assertEquals(-50, viewModel.uiState.value.modifier)
    }

    @Test
    fun testRemoveDieFromEmptyPoolEdgeCase() {
        val viewModel = DiceViewModel()
        viewModel.clearPool()
        assertEquals(0, viewModel.uiState.value.totalDiceCount)

        // Removing from empty pool should not crash
        viewModel.removeDie(RpgDieType.D20)
        assertEquals(0, viewModel.uiState.value.totalDiceCount)
    }

    @Test
    fun testRollWithEmptyPoolDoesNotRoll() = runTest(testDispatcher) {
        val viewModel = DiceViewModel()
        viewModel.clearPool()
        assertEquals(0, viewModel.uiState.value.totalDiceCount)

        viewModel.rollDice()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRolling)
        assertNull(viewModel.uiState.value.lastRollRecord)
    }

    @Test
    fun testPresetsSuccess() {
        val viewModel = DiceViewModel()
        val potionPreset = viewModel.availablePresets.first { it.id == "potion" }
        viewModel.applyPreset(potionPreset)

        assertEquals(2, viewModel.uiState.value.poolCounts[RpgDieType.D4])
        assertEquals(2, viewModel.uiState.value.modifier)
        assertEquals(RollMode.NORMAL, viewModel.uiState.value.rollMode)
        assertEquals(potionPreset, viewModel.uiState.value.activePreset)
    }

    @Test
    fun testRollModesSuccess() {
        val viewModel = DiceViewModel()
        viewModel.setRollMode(RollMode.ADVANTAGE)
        assertEquals(RollMode.ADVANTAGE, viewModel.uiState.value.rollMode)

        viewModel.setRollMode(RollMode.DISADVANTAGE)
        assertEquals(RollMode.DISADVANTAGE, viewModel.uiState.value.rollMode)

        viewModel.setRollMode(RollMode.DROP_LOWEST)
        assertEquals(RollMode.DROP_LOWEST, viewModel.uiState.value.rollMode)
    }

    @Test
    fun testSettingsTogglesSuccess() {
        val viewModel = DiceViewModel()

        viewModel.toggleShakeEnabled(true)
        assertTrue(viewModel.uiState.value.isShakeEnabled)

        viewModel.toggleSoundEnabled(false)
        assertFalse(viewModel.uiState.value.isSoundEnabled)

        viewModel.toggleHapticsEnabled(false)
        assertFalse(viewModel.uiState.value.isHapticsEnabled)

        viewModel.updateShakeSensitivity(ShakeSensitivity.HIGH)
        assertEquals(ShakeSensitivity.HIGH, viewModel.uiState.value.shakeSensitivity)
    }

    @Test
    fun testHistoryManagementSuccess() = runTest(testDispatcher) {
        val viewModel = DiceViewModel()
        viewModel.clearPool()
        viewModel.addDie(RpgDieType.D20)
        viewModel.rollDice()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.rollHistory.size)
        val recordId = viewModel.uiState.value.rollHistory.first().id

        viewModel.deleteRoll(recordId)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.rollHistory.size)
    }
}
