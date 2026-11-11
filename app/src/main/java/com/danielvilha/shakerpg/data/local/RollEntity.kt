package com.danielvilha.shakerpg.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.danielvilha.shakerpg.model.DieRollResult
import com.danielvilha.shakerpg.model.RollMode
import com.danielvilha.shakerpg.model.RollRecord
import com.danielvilha.shakerpg.model.RpgDieType

@Entity(tableName = "roll_history")
data class RollEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val formula: String,
    val serializedDiceResults: String,
    val modifier: Int,
    val total: Int,
    val isCritSuccess: Boolean,
    val isCritFail: Boolean,
    val rollModeName: String,
    val presetName: String? = null
) {
    fun toRollRecord(): RollRecord {
        val dice = if (serializedDiceResults.isBlank()) {
            emptyList()
        } else {
            serializedDiceResults.split(";").mapNotNull { entry ->
                val tokens = entry.split(":")
                if (tokens.size >= 5) {
                    val dieType = try {
                        RpgDieType.valueOf(tokens[0])
                    } catch (_: Exception) {
                        RpgDieType.D20
                    }
                    val value = tokens[1].toIntOrNull() ?: 0
                    val isDropped = tokens[2].toBooleanStrictOrNull() ?: false
                    val isCritSuccess = tokens[3].toBooleanStrictOrNull() ?: false
                    val isCritFail = tokens[4].toBooleanStrictOrNull() ?: false
                    DieRollResult(
                        dieType = dieType,
                        value = value,
                        isDropped = isDropped,
                        isCritSuccess = isCritSuccess,
                        isCritFail = isCritFail
                    )
                } else null
            }
        }

        val mode = try {
            RollMode.valueOf(rollModeName)
        } catch (_: Exception) {
            RollMode.NORMAL
        }

        return RollRecord(
            id = id,
            timestamp = timestamp,
            formula = formula,
            diceResults = dice,
            modifier = modifier,
            total = total,
            isCritSuccess = isCritSuccess,
            isCritFail = isCritFail,
            rollMode = mode,
            presetName = presetName
        )
    }

    companion object {
        fun fromRollRecord(record: RollRecord): RollEntity {
            val serialized = record.diceResults.joinToString(";") {
                "${it.dieType.name}:${it.value}:${it.isDropped}:${it.isCritSuccess}:${it.isCritFail}"
            }
            return RollEntity(
                id = record.id,
                timestamp = record.timestamp,
                formula = record.formula,
                serializedDiceResults = serialized,
                modifier = record.modifier,
                total = record.total,
                isCritSuccess = record.isCritSuccess,
                isCritFail = record.isCritFail,
                rollModeName = record.rollMode.name,
                presetName = record.presetName
            )
        }
    }
}
