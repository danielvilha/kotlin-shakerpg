package com.danielvilha.shakerpg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielvilha.shakerpg.data.repository.RollRepository
import com.danielvilha.shakerpg.model.DieInstance
import com.danielvilha.shakerpg.model.DieRollResult
import com.danielvilha.shakerpg.model.RollMode
import com.danielvilha.shakerpg.model.RollPreset
import com.danielvilha.shakerpg.model.RollRecord
import com.danielvilha.shakerpg.model.RpgDieType
import com.danielvilha.shakerpg.sensor.ShakeSensitivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

sealed interface DiceRollEvent {
    data object TumbleTick : DiceRollEvent
    data class Settle(val isCritSuccess: Boolean, val isCritFail: Boolean) : DiceRollEvent
    data object ButtonTap : DiceRollEvent
}

class DiceViewModel(
    private var repository: RollRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiceUiState())
    val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DiceRollEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<DiceRollEvent> = _events.asSharedFlow()

    private var rollJob: Job? = null
    private var historyObservationJob: Job? = null

    init {
        repository?.let { setRepository(it) }
    }

    fun setRepository(repo: RollRepository) {
        this.repository = repo
        historyObservationJob?.cancel()
        historyObservationJob = viewModelScope.launch {
            repo.allRolls.collect { records ->
                _uiState.update { state ->
                    val totalRolls = records.size
                    val nat20Count = records.count { it.isCritSuccess }
                    val nat1Count = records.count { it.isCritFail }
                    val highestTotal = records.maxOfOrNull { it.total } ?: 0
                    state.copy(
                        rollHistory = records,
                        stats = DiceStats(
                            totalRolls = totalRolls,
                            nat20Count = nat20Count,
                            nat1Count = nat1Count,
                            highestTotal = highestTotal
                        )
                    )
                }
            }
        }
    }

    val availablePresets: List<RollPreset> = listOf(
        RollPreset("d20", "Standard d20", "Basic d20 check", mapOf(RpgDieType.D20 to 1)),
        RollPreset("adv", "Advantage", "Roll 2d20, keep highest", mapOf(RpgDieType.D20 to 2), rollMode = RollMode.ADVANTAGE),
        RollPreset("disadv", "Disadvantage", "Roll 2d20, keep lowest", mapOf(RpgDieType.D20 to 2), rollMode = RollMode.DISADVANTAGE),
        RollPreset("stat", "Stat Roll", "4d6 drop the lowest", mapOf(RpgDieType.D6 to 4), rollMode = RollMode.DROP_LOWEST),
        RollPreset("gs", "Greatsword", "2d6 slashing damage", mapOf(RpgDieType.D6 to 2)),
        RollPreset("fb", "Fireball", "8d6 radiant flame damage", mapOf(RpgDieType.D6 to 8)),
        RollPreset("potion", "Healing Potion", "2d4 + 2 hit points", mapOf(RpgDieType.D4 to 2), modifier = 2),
        RollPreset("cure", "Cure Wounds", "1d8 + 3 spell heal", mapOf(RpgDieType.D8 to 1), modifier = 3),
        RollPreset("pct", "Percentile (d100)", "1d100 check", mapOf(RpgDieType.D100 to 1))
    )

    fun addDie(dieType: RpgDieType) {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        _uiState.update { state ->
            val currentCount = state.poolCounts[dieType] ?: 0
            if (state.totalDiceCount >= 20) return@update state
            val newCounts = state.poolCounts + (dieType to (currentCount + 1))
            val updatedInstances = buildInitialInstancesFromCounts(newCounts)
            state.copy(
                poolCounts = newCounts,
                diceInstances = updatedInstances,
                activePreset = null
            )
        }
    }

    fun removeDie(dieType: RpgDieType) {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        _uiState.update { state ->
            val currentCount = state.poolCounts[dieType] ?: 0
            if (currentCount <= 0) return@update state
            val newCounts = if (currentCount == 1) {
                state.poolCounts - dieType
            } else {
                state.poolCounts + (dieType to (currentCount - 1))
            }
            val updatedInstances = buildInitialInstancesFromCounts(newCounts)
            state.copy(
                poolCounts = newCounts,
                diceInstances = updatedInstances,
                activePreset = null
            )
        }
    }

    fun clearPool() {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        _uiState.update { state ->
            state.copy(
                poolCounts = emptyMap(),
                diceInstances = emptyList(),
                modifier = 0,
                rollMode = RollMode.NORMAL,
                activePreset = null,
                lastRollRecord = null
            )
        }
    }

    fun setModifier(newMod: Int) {
        _uiState.update { it.copy(modifier = newMod.coerceIn(-50, 99)) }
    }

    fun incrementModifier() {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        _uiState.update { it.copy(modifier = (it.modifier + 1).coerceAtMost(99)) }
    }

    fun decrementModifier() {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        _uiState.update { it.copy(modifier = (it.modifier - 1).coerceAtLeast(-50)) }
    }

    fun setRollMode(mode: RollMode) {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        _uiState.update { it.copy(rollMode = mode) }
    }

    fun applyPreset(preset: RollPreset) {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        val instances = buildInitialInstancesFromCounts(preset.diceCounts)
        _uiState.update {
            it.copy(
                poolCounts = preset.diceCounts,
                diceInstances = instances,
                modifier = preset.modifier,
                rollMode = preset.rollMode,
                activePreset = preset
            )
        }
    }

    fun rollDice() {
        if (_uiState.value.isRolling) return
        if (_uiState.value.totalDiceCount == 0) {
            addDie(RpgDieType.D20)
        }

        rollJob?.cancel()
        rollJob = viewModelScope.launch {
            _uiState.update { it.copy(isRolling = true) }

            val currentState = _uiState.value
            val diceTypes = mutableListOf<RpgDieType>()
            currentState.poolCounts.forEach { (die, count) ->
                repeat(count) { diceTypes.add(die) }
            }

            // Tumble animation loop (~650ms, 8 ticks)
            val iterations = 8
            for (step in 1..iterations) {
                _events.tryEmit(DiceRollEvent.TumbleTick)
                val tumblingInstances = diceTypes.mapIndexed { index, dieType ->
                    DieInstance(
                        id = "die-$index",
                        dieType = dieType,
                        currentValue = dieType.roll(),
                        isRolling = true,
                        rotationAngle = Random.nextFloat() * 50f - 25f
                    )
                }
                _uiState.update { it.copy(diceInstances = tumblingInstances) }
                delay(75L)
            }

            // Final settled roll calculation
            val finalValues = diceTypes.map { it.roll() }
            val mode = currentState.rollMode

            val droppedIndices = mutableSetOf<Int>()
            if (diceTypes.size >= 2) {
                when (mode) {
                    RollMode.ADVANTAGE -> {
                        val maxVal = finalValues.maxOrNull() ?: 0
                        var keptOne = false
                        finalValues.forEachIndexed { idx, v ->
                            if (v == maxVal && !keptOne) {
                                keptOne = true
                            } else {
                                droppedIndices.add(idx)
                            }
                        }
                    }
                    RollMode.DISADVANTAGE -> {
                        val minVal = finalValues.minOrNull() ?: 0
                        var keptOne = false
                        finalValues.forEachIndexed { idx, v ->
                            if (v == minVal && !keptOne) {
                                keptOne = true
                            } else {
                                droppedIndices.add(idx)
                            }
                        }
                    }
                    RollMode.DROP_LOWEST -> {
                        val minVal = finalValues.minOrNull() ?: 0
                        val minIndex = finalValues.indexOf(minVal)
                        if (minIndex >= 0) droppedIndices.add(minIndex)
                    }
                    RollMode.NORMAL -> {}
                }
            }

            var hasCritSuccess = false
            var hasCritFail = false

            val finalInstances = diceTypes.mapIndexed { idx, dieType ->
                val value = finalValues[idx]
                val isDropped = droppedIndices.contains(idx)
                val isD20 = dieType == RpgDieType.D20
                val critSuccess = isD20 && value == 20 && !isDropped
                val critFail = isD20 && value == 1 && !isDropped

                if (critSuccess) hasCritSuccess = true
                if (critFail) hasCritFail = true

                DieInstance(
                    id = "die-$idx",
                    dieType = dieType,
                    currentValue = value,
                    isRolling = false,
                    rotationAngle = (Random.nextFloat() * 16f - 8f),
                    isCritSuccess = critSuccess,
                    isCritFail = critFail,
                    isDropped = isDropped
                )
            }

            val activeDiceSum = finalValues.filterIndexed { idx, _ -> !droppedIndices.contains(idx) }.sum()
            val finalTotal = activeDiceSum + currentState.modifier

            val record = RollRecord(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                formula = currentState.formulaString,
                diceResults = finalInstances.map {
                    DieRollResult(
                        dieType = it.dieType,
                        value = it.currentValue,
                        isDropped = it.isDropped,
                        isCritSuccess = it.isCritSuccess,
                        isCritFail = it.isCritFail
                    )
                },
                modifier = currentState.modifier,
                total = finalTotal,
                isCritSuccess = hasCritSuccess,
                isCritFail = hasCritFail,
                rollMode = currentState.rollMode,
                presetName = currentState.activePreset?.name
            )

            // Save to Room database if repository is configured
            if (repository != null) {
                repository?.insertRoll(record)
                _uiState.update {
                    it.copy(
                        isRolling = false,
                        diceInstances = finalInstances,
                        lastRollRecord = record
                    )
                }
            } else {
                // In-memory fallback (e.g. unit tests without Room)
                val oldStats = currentState.stats
                val newStats = oldStats.copy(
                    totalRolls = oldStats.totalRolls + 1,
                    nat20Count = oldStats.nat20Count + (if (hasCritSuccess) 1 else 0),
                    nat1Count = oldStats.nat1Count + (if (hasCritFail) 1 else 0),
                    highestTotal = maxOf(oldStats.highestTotal, finalTotal)
                )

                _uiState.update {
                    it.copy(
                        isRolling = false,
                        diceInstances = finalInstances,
                        lastRollRecord = record,
                        rollHistory = listOf(record) + it.rollHistory.take(24),
                        stats = newStats
                    )
                }
            }

            _events.tryEmit(DiceRollEvent.Settle(hasCritSuccess, hasCritFail))
        }
    }

    fun reRollRecord(record: RollRecord) {
        val diceCounts = mutableMapOf<RpgDieType, Int>()
        record.diceResults.forEach { res ->
            diceCounts[res.dieType] = (diceCounts[res.dieType] ?: 0) + 1
        }
        _uiState.update {
            it.copy(
                poolCounts = diceCounts,
                modifier = record.modifier,
                rollMode = record.rollMode
            )
        }
        rollDice()
    }

    fun deleteRoll(id: String) {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        viewModelScope.launch {
            if (repository != null) {
                repository?.deleteRollById(id)
            } else {
                _uiState.update { state ->
                    val updated = state.rollHistory.filter { it.id != id }
                    state.copy(rollHistory = updated)
                }
            }
        }
    }

    fun clearHistory() {
        _events.tryEmit(DiceRollEvent.ButtonTap)
        viewModelScope.launch {
            if (repository != null) {
                repository?.clearAll()
            } else {
                _uiState.update { it.copy(rollHistory = emptyList()) }
            }
        }
    }

    fun updateShakeSensitivity(sensitivity: ShakeSensitivity) {
        _uiState.update { it.copy(shakeSensitivity = sensitivity) }
    }

    fun toggleShakeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isShakeEnabled = enabled) }
    }

    fun toggleSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isSoundEnabled = enabled) }
    }

    fun toggleHapticsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isHapticsEnabled = enabled) }
    }

    private fun buildInitialInstancesFromCounts(counts: Map<RpgDieType, Int>): List<DieInstance> {
        val instances = mutableListOf<DieInstance>()
        var index = 0
        counts.forEach { (die, count) ->
            repeat(count) {
                instances.add(
                    DieInstance(
                        id = "die-${die.name}-$index",
                        dieType = die,
                        currentValue = die.sides,
                        isRolling = false
                    )
                )
                index++
            }
        }
        return instances
    }
}
