package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "REXER",
    val userRole: String = "Software Engineer & Creator",
    val dailyBudgetLimit: Double = 3000.0,
    val monthlyBudgetLimit: Double = 90000.0,
    val currencySymbol: String = "₹",
    val pinCode: String = "1234",
    val isPinAuthEnabled: Boolean = false,
    val isAppLocked: Boolean = false,
    val lastNudgeTime: Long = 0L,
    val totalSavingsSaved: Double = 14500.0
)
