package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coach_messages")
data class CoachMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "coach"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "nudge" // "nudge", "analysis", "chat", "tip"
)
