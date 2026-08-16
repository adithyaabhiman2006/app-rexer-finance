package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CoachMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachMessageDao {
    @Query("SELECT * FROM coach_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<CoachMessageEntity>>

    @Query("SELECT * FROM coach_messages WHERE type = 'nudge' ORDER BY timestamp DESC LIMIT 1")
    fun getLatestNudge(): Flow<CoachMessageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CoachMessageEntity): Long

    @Query("DELETE FROM coach_messages")
    suspend fun clearChat()
}
