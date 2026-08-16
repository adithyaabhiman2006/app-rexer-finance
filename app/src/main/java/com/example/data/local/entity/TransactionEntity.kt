package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String, // "Food & Nutrition", "Tech Gear & Setup", "Server & Cloud", "Trading & Subs", "Content & Studio", "Bike & Transport", "Lifestyle"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val goalId: Long? = null
)
