package com.danielvilha.shakerpg.viewmodel

import com.danielvilha.shakerpg.model.DieInstance
import com.danielvilha.shakerpg.model.RollMode
import com.danielvilha.shakerpg.model.RollPreset
import com.danielvilha.shakerpg.model.RollRecord
import com.danielvilha.shakerpg.model.RpgDieType
import com.danielvilha.shakerpg.sensor.ShakeSensitivity

data class DiceStats(
    val totalRolls: Int = 0,
    val nat20Count: Int = 0,
    val nat1Count: Int = 0,
    val highestTotal: Int = 0
)

data class DiceUiState(
    val poolCounts: Map<RpgDieType, Int> = mapOf(RpgDieType.D20 to 1),
    val modifier: Int = 0,
    val rollMode: RollMode = RollMode.NORMAL,
    val isRolling: Boolean = false,
    val diceInstances: List<DieInstance> = listOf(
        DieInstance(
            id = "initial-d20",
            dieType = RpgDieType.D20,
            currentValue = 20,
            isCritSuccess = true
        )
    ),
    val lastRollRecord: RollRecord? = null,
    val rollHistory: List<RollRecord> = emptyList(),
    val shakeSensitivity: ShakeSensitivity = ShakeSensitivity.MEDIUM,
    val isShakeEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val activePreset: RollPreset? = null,
    val stats: DiceStats = DiceStats()
) {
    val totalDiceCount: Int
        get() = poolCounts.values.sum()

    val formulaString: String
        get() {
            val parts = poolCounts.filterValues { it > 0 }.entries
                .sortedBy { it.key.sides }
                .map { "${it.value}${it.key.label}" }
                .toMutableList()

            if (parts.isEmpty()) return "0 dice"

            val dicePart = parts.joinToString(" + ")
            return when {
                modifier > 0 -> "$dicePart + $modifier"
                modifier < 0 -> "$dicePart - ${kotlin.math.abs(modifier)}"
                else -> dicePart
            }
        }
}
