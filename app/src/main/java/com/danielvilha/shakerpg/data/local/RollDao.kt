package com.danielvilha.shakerpg.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RollDao {
    @Query("SELECT * FROM roll_history ORDER BY timestamp DESC")
    fun getAllRolls(): Flow<List<RollEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoll(roll: RollEntity)

    @Query("DELETE FROM roll_history WHERE id = :id")
    suspend fun deleteRollById(id: String)

    @Query("DELETE FROM roll_history")
    suspend fun clearAll()
}
