package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val category: String, // "Vehicle", "Trading", "Brand", "Hardware", "Career"
    val deadline: String = "2026-12-31",
    val colorHex: String = "#FF334B",
    val iconType: String = "bike", // "bike", "chart", "brand", "code", "star"
    val description: String = ""
)
