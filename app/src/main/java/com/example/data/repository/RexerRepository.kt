package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CoachMessageEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.ReminderTaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class RexerRepository(private val database: AppDatabase) {
    private val transactionDao = database.transactionDao()
    private val goalDao = database.goalDao()
    private val reminderTaskDao = database.reminderTaskDao()
    private val coachMessageDao = database.coachMessageDao()
    private val userSettingsDao = database.userSettingsDao()

    // --- Transactions ---
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getWeekStartTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getTodayTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getTodayTransactions(getTodayStartTimestamp())

    fun getTodaySpent(): Flow<Double?> =
        transactionDao.getTodaySpentFlow(getTodayStartTimestamp())

    fun getWeekSpent(): Flow<Double?> =
        transactionDao.getWeekSpentFlow(getWeekStartTimestamp())

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        val id = transactionDao.insertTransaction(transaction)
        // If linked to a goal as a contribution, update goal currentAmount
        transaction.goalId?.let { gId ->
            goalDao.contributeToGoal(gId, transaction.amount)
        }
        return id
    }

    suspend fun deleteTransaction(id: Long) = transactionDao.deleteTransactionById(id)

    // --- Goals ---
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()

    suspend fun addGoal(goal: GoalEntity): Long = goalDao.insertGoal(goal)

    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)

    suspend fun contributeToGoal(goalId: Long, amount: Double) {
        goalDao.contributeToGoal(goalId, amount)
        // Also record as a savings/goal transaction
        goalDao.getGoalById(goalId)?.let { goal ->
            transactionDao.insertTransaction(
                TransactionEntity(
                    title = "Goal Deposit: ${goal.title}",
                    amount = amount,
                    category = "Savings & Goals",
                    note = "Direct allocation towards ${goal.title}",
                    goalId = goalId
                )
            )
        }
    }

    suspend fun deleteGoal(id: Long) = goalDao.deleteGoalById(id)

    // --- Reminders & Tasks ---
    val allTasks: Flow<List<ReminderTaskEntity>> = reminderTaskDao.getAllReminderTasks()

    suspend fun addTask(task: ReminderTaskEntity): Long = reminderTaskDao.insertTask(task)

    suspend fun toggleTaskCompleted(id: Long, completed: Boolean) =
        reminderTaskDao.setTaskCompleted(id, completed)

    suspend fun deleteTask(id: Long) = reminderTaskDao.deleteTaskById(id)

    // --- Coach Messages ---
    val allCoachMessages: Flow<List<CoachMessageEntity>> = coachMessageDao.getAllMessages()
    val latestNudge: Flow<CoachMessageEntity?> = coachMessageDao.getLatestNudge()

    suspend fun addCoachMessage(message: CoachMessageEntity): Long =
        coachMessageDao.insertMessage(message)

    suspend fun clearCoachChat() = coachMessageDao.clearChat()

    // --- User Settings ---
    val userSettings: Flow<UserSettingsEntity?> = userSettingsDao.getUserSettings()

    suspend fun updateDailyLimit(newLimit: Double) =
        userSettingsDao.updateDailyLimit(newLimit)

    suspend fun updateSettings(settings: UserSettingsEntity) =
        userSettingsDao.update(settings)

    suspend fun setAppLocked(locked: Boolean) =
        userSettingsDao.setAppLocked(locked)

    suspend fun setPinAuth(pin: String, enabled: Boolean) =
        userSettingsDao.setPinAuth(pin, enabled)
}
