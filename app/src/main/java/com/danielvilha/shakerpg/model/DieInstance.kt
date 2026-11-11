package com.danielvilha.shakerpg.model

enum class RollMode(val title: String, val description: String) {
    NORMAL("Normal", "Sum of all active dice"),
    ADVANTAGE("Advantage", "Roll twice, keep the highest"),
    DISADVANTAGE("Disadvantage", "Roll twice, keep the lowest"),
    DROP_LOWEST("Drop Lowest", "Drop the lowest die result")
}

data class DieInstance(
    val id: String,
    val dieType: RpgDieType,
    val currentValue: Int,
    val isRolling: Boolean = false,
    val rotationAngle: Float = 0f,
    val isCritSuccess: Boolean = false,
    val isCritFail: Boolean = false,
    val isDropped: Boolean = false
)

data class RollRecord(
    val id: String,
    val timestamp: Long,
    val formula: String,
    val diceResults: List<DieRollResult>,
    val modifier: Int,
    val total: Int,
    val isCritSuccess: Boolean,
    val isCritFail: Boolean,
    val rollMode: RollMode = RollMode.NORMAL,
    val presetName: String? = null
)

data class DieRollResult(
    val dieType: RpgDieType,
    val value: Int,
    val isDropped: Boolean = false,
    val isCritSuccess: Boolean = false,
    val isCritFail: Boolean = false
)

data class RollPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val diceCounts: Map<RpgDieType, Int>,
    val modifier: Int = 0,
    val rollMode: RollMode = RollMode.NORMAL
)
