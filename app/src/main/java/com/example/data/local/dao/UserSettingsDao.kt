package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: UserSettingsEntity)

    @Update
    suspend fun update(settings: UserSettingsEntity)

    @Query("UPDATE user_settings SET dailyBudgetLimit = :limit WHERE id = 1")
    suspend fun updateDailyLimit(limit: Double)

    @Query("UPDATE user_settings SET currencySymbol = :currency WHERE id = 1")
    suspend fun updateCurrency(currency: String)

    @Query("UPDATE user_settings SET isAppLocked = :locked WHERE id = 1")
    suspend fun setAppLocked(locked: Boolean)

    @Query("UPDATE user_settings SET pinCode = :pin, isPinAuthEnabled = :enabled WHERE id = 1")
    suspend fun setPinAuth(pin: String, enabled: Boolean)
}
