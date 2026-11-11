package com.danielvilha.shakerpg.data.repository

import com.danielvilha.shakerpg.data.local.RollDao
import com.danielvilha.shakerpg.data.local.RollEntity
import com.danielvilha.shakerpg.model.RollRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RollRepository(private val rollDao: RollDao) {

    val allRolls: Flow<List<RollRecord>> = rollDao.getAllRolls().map { entities ->
        entities.map { it.toRollRecord() }
    }

    suspend fun insertRoll(record: RollRecord) {
        rollDao.insertRoll(RollEntity.fromRollRecord(record))
    }

    suspend fun deleteRollById(id: String) {
        rollDao.deleteRollById(id)
    }

    suspend fun clearAll() {
        rollDao.clearAll()
    }
}
