package com.danielvilha.shakerpg

import com.danielvilha.shakerpg.data.local.RollEntity
import com.danielvilha.shakerpg.model.DieRollResult
import com.danielvilha.shakerpg.model.RollMode
import com.danielvilha.shakerpg.model.RollRecord
import com.danielvilha.shakerpg.model.RpgDieType
import com.danielvilha.shakerpg.viewmodel.DiceViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testDiceSidesAndRollRange() {
    RpgDieType.entries.forEach { die ->
      repeat(50) {
        val result = die.roll()
        assertTrue("Die ${die.label} roll $result out of bounds [1, ${die.sides}]", result in 1..die.sides)
      }
    }
  }

  @Test
  fun testDicePoolManagementAndFormula() {
    val viewModel = DiceViewModel()
    viewModel.clearPool()
    assertEquals("0 dice", viewModel.uiState.value.formulaString)

    viewModel.addDie(RpgDieType.D20)
    assertEquals("1d20", viewModel.uiState.value.formulaString)

    viewModel.addDie(RpgDieType.D6)
    viewModel.addDie(RpgDieType.D6)
    assertEquals("2d6 + 1d20", viewModel.uiState.value.formulaString)

    viewModel.setModifier(4)
    assertEquals("2d6 + 1d20 + 4", viewModel.uiState.value.formulaString)

    viewModel.setModifier(-2)
    assertEquals("2d6 + 1d20 - 2", viewModel.uiState.value.formulaString)

    viewModel.removeDie(RpgDieType.D6)
    assertEquals("1d6 + 1d20 - 2", viewModel.uiState.value.formulaString)
  }

  @Test
  fun testPresetApplication() {
    val viewModel = DiceViewModel()
    val fireballPreset = viewModel.availablePresets.first { it.id == "fb" }
    viewModel.applyPreset(fireballPreset)

    assertEquals(8, viewModel.uiState.value.poolCounts[RpgDieType.D6])
    assertEquals("8d6", viewModel.uiState.value.formulaString)
    assertEquals(RollMode.NORMAL, viewModel.uiState.value.rollMode)
  }

  @Test
  fun testRollEntitySerializationRoundTrip() {
    val record = RollRecord(
      id = "test-123",
      timestamp = 1690000000L,
      formula = "1d20 + 2d6 + 3",
      diceResults = listOf(
        DieRollResult(dieType = RpgDieType.D20, value = 20, isCritSuccess = true),
        DieRollResult(dieType = RpgDieType.D6, value = 4),
        DieRollResult(dieType = RpgDieType.D6, value = 5)
      ),
      modifier = 3,
      total = 32,
      isCritSuccess = true,
      isCritFail = false,
      rollMode = RollMode.NORMAL,
      presetName = "Custom"
    )

    val entity = RollEntity.fromRollRecord(record)
    val restored = entity.toRollRecord()

    assertEquals(record.id, restored.id)
    assertEquals(record.total, restored.total)
    assertEquals(record.formula, restored.formula)
    assertTrue(restored.isCritSuccess)
    assertFalse(restored.isCritFail)
    assertEquals(3, restored.diceResults.size)
    assertEquals(RpgDieType.D20, restored.diceResults[0].dieType)
    assertEquals(20, restored.diceResults[0].value)
  }
}
