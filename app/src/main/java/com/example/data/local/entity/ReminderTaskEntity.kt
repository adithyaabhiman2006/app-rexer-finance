package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_tasks")
data class ReminderTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val scheduledTime: String = "09:00 AM",
    val category: String = "Finance", // "Finance", "Trading", "Figma & UI", "Dev & Code", "Lifestyle"
    val priority: String = "HIGH", // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean = false,
    val isDailyPushEnabled: Boolean = true,
    val integrationSource: String = "Google Tasks" // "Google Tasks", "Google Calendar", "REXER System"
)
