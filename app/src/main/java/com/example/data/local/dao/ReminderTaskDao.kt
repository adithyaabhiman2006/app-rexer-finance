package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ReminderTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderTaskDao {
    @Query("SELECT * FROM reminder_tasks ORDER BY isCompleted ASC, id ASC")
    fun getAllReminderTasks(): Flow<List<ReminderTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ReminderTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<ReminderTaskEntity>)

    @Update
    suspend fun updateTask(task: ReminderTaskEntity)

    @Query("UPDATE reminder_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM reminder_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
}
