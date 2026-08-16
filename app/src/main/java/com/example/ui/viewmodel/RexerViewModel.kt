package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CoachMessageEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.ReminderTaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserSettingsEntity
import com.example.data.remote.GeminiCoachService
import com.example.data.repository.RexerRepository
import com.example.receiver.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RexerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RexerRepository
    private val geminiService = GeminiCoachService()

    val allTransactions: StateFlow<List<TransactionEntity>>
    val todayTransactions: StateFlow<List<TransactionEntity>>
    val todaySpent: StateFlow<Double?>
    val weekSpent: StateFlow<Double?>
    val allGoals: StateFlow<List<GoalEntity>>
    val allTasks: StateFlow<List<ReminderTaskEntity>>
    val userSettings: StateFlow<UserSettingsEntity?>
    val allCoachMessages: StateFlow<List<CoachMessageEntity>>
    val latestNudge: StateFlow<CoachMessageEntity?>

    private val _isGeneratingCoach = MutableStateFlow(false)
    val isGeneratingCoach: StateFlow<Boolean> = _isGeneratingCoach.asStateFlow()

    private val _isAppLockedState = MutableStateFlow(false)
    val isAppLockedState: StateFlow<Boolean> = _isAppLockedState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = RexerRepository(database)

        allTransactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        todayTransactions = repository.getTodayTransactions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        todaySpent = repository.getTodaySpent()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1450.0)

        weekSpent = repository.getWeekSpent()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4100.0)

        allGoals = repository.allGoals
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTasks = repository.allTasks
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        userSettings = repository.userSettings
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        allCoachMessages = repository.allCoachMessages
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        latestNudge = repository.latestNudge
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureInitialDataSeeded()
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        note: String = "",
        goalId: Long? = null
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                title = title.trim(),
                amount = amount,
                category = category,
                note = note.trim(),
                timestamp = System.currentTimeMillis(),
                goalId = goalId
            )
            repository.addTransaction(tx)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun contributeToGoal(goalId: Long, amount: Double) {
        viewModelScope.launch {
            repository.contributeToGoal(goalId, amount)
        }
    }

    fun createGoal(
        title: String,
        targetAmount: Double,
        currentAmount: Double,
        category: String,
        deadline: String,
        colorHex: String,
        iconType: String,
        description: String
    ) {
        viewModelScope.launch {
            val goal = GoalEntity(
                title = title.trim(),
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                category = category,
                deadline = deadline,
                colorHex = colorHex,
                iconType = iconType,
                description = description.trim()
            )
            repository.addGoal(goal)
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }

    fun addTask(
        title: String,
        description: String,
        time: String,
        category: String,
        priority: String,
        integrationSource: String
    ) {
        viewModelScope.launch {
            val task = ReminderTaskEntity(
                title = title.trim(),
                description = description.trim(),
                scheduledTime = time,
                category = category,
                priority = priority,
                isCompleted = false,
                isDailyPushEnabled = true,
                integrationSource = integrationSource
            )
            repository.addTask(task)
        }
    }

    fun toggleTask(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(id, completed)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun updateDailyLimit(newLimit: Double) {
        viewModelScope.launch {
            repository.updateDailyLimit(newLimit)
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            repository.updateCurrency(currency)
        }
    }

    fun triggerTaskNotification(task: ReminderTaskEntity) {
        NotificationHelper.showNotification(
            context = getApplication(),
            notificationId = (task.id % 10000).toInt(),
            title = "⏰ ${task.title}",
            message = if (task.description.isNotBlank()) task.description else "Scheduled for ${task.scheduledTime} [${task.integrationSource}]",
            category = task.category
        )
    }

    fun generateAiCoachAdvice(userQuery: String? = null) {
        viewModelScope.launch {
            _isGeneratingCoach.value = true
            try {
                val currentTodaySpent = todaySpent.value ?: 0.0
                val settings = userSettings.value
                val limit = settings?.dailyBudgetLimit ?: 3000.0
                val currentWeekSpent = weekSpent.value ?: currentTodaySpent
                val goals = allGoals.value
                val tasks = allTasks.value
                val recentTx = todayTransactions.value

                // If user entered a question, save user query in message list
                if (!userQuery.isNullOrBlank()) {
                    repository.addCoachMessage(
                        CoachMessageEntity(
                            sender = "user",
                            content = userQuery,
                            timestamp = System.currentTimeMillis(),
                            type = "chat"
                        )
                    )
                }

                val aiResult = geminiService.generateCoachAnalysis(
                    todaySpent = currentTodaySpent,
                    dailyLimit = limit,
                    weekSpent = currentWeekSpent,
                    recentTransactions = recentTx,
                    goals = goals,
                    pendingTasks = tasks.filter { !it.isCompleted },
                    userCustomQuery = userQuery
                )

                repository.addCoachMessage(
                    CoachMessageEntity(
                        sender = "coach",
                        content = aiResult,
                        timestamp = System.currentTimeMillis(),
                        type = if (userQuery.isNullOrBlank()) "nudge" else "analysis"
                    )
                )
            } finally {
                _isGeneratingCoach.value = false
            }
        }
    }

    fun updateSettings(settings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    fun lockApp(locked: Boolean) {
        _isAppLockedState.value = locked
    }
}
