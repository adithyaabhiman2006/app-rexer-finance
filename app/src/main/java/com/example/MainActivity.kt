package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.ui.components.AdjustLimitDialog
import com.example.ui.components.DepositDialog
import com.example.ui.components.PinLockScreen
import com.example.ui.components.ProfileAuthDialog
import com.example.ui.components.QuickExpenseSheet
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RexerViewModel

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Speed, Icons.Outlined.Speed),
    GOALS("Goals", Icons.Filled.Flag, Icons.Outlined.Flag),
    FINANCE("Finance", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    SCHEDULE("Schedule", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    AI_COACH("Coach", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
}

class MainActivity : ComponentActivity() {
    private val viewModel: RexerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RexerAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RexerAppContent(viewModel: RexerViewModel) {
    var selectedTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }
    var showQuickExpenseSheet by remember { mutableStateOf(false) }
    var showAdjustLimitDialog by remember { mutableStateOf(false) }
    var showProfileAuthDialog by remember { mutableStateOf(false) }
    var isAppUnlocked by remember { mutableStateOf(false) }
    var targetGoalForContribution by remember { mutableStateOf<GoalEntity?>(null) }

    val todaySpent by viewModel.todaySpent.collectAsState()
    val weekSpent by viewModel.weekSpent.collectAsState()
    val todayTransactions by viewModel.todayTransactions.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val allGoals by viewModel.allGoals.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val latestNudge by viewModel.latestNudge.collectAsState()
    val allCoachMessages by viewModel.allCoachMessages.collectAsState()
    val isGeneratingCoach by viewModel.isGeneratingCoach.collectAsState()

    val currencySymbol = userSettings?.currencySymbol ?: "₹"
    val dailyLimit = userSettings?.dailyBudgetLimit ?: 3000.0

    val requiresPinLock = userSettings?.isPinAuthEnabled == true && !isAppUnlocked && !userSettings?.pinCode.isNullOrBlank()

    if (requiresPinLock) {
        PinLockScreen(
            correctPin = userSettings?.pinCode ?: "1234",
            onUnlockSuccess = { isAppUnlocked = true }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(CarbonBlack),
            containerColor = CarbonBlack,
            contentWindowInsets = WindowInsets.navigationBars,
            floatingActionButton = {
                if (selectedTab != NavigationTab.AI_COACH) {
                    FloatingActionButton(
                        onClick = { showQuickExpenseSheet = true },
                        containerColor = NeonRed,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        shape = CircleShape,
                        modifier = Modifier.testTag("global_fab_add_expense")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Daily Expense",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CarbonDark,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    NavigationBar(
                        containerColor = CarbonDark,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(68.dp)
                    ) {
                        NavigationTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) (if (tab == NavigationTab.AI_COACH) CyberCyan else NeonRed) else TextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextPrimary else TextMuted,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = if (tab == NavigationTab.AI_COACH) CyberCyan.copy(alpha = 0.15f) else NeonRed.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    NavigationTab.DASHBOARD -> {
                        DashboardScreen(
                            todaySpent = todaySpent ?: 0.0,
                            dailyLimit = dailyLimit,
                            currencySymbol = currencySymbol,
                            userSettings = userSettings,
                            todayTransactions = todayTransactions,
                            goals = allGoals,
                            tasks = allTasks,
                            latestNudge = latestNudge,
                            isGeneratingCoach = isGeneratingCoach,
                            onOpenQuickExpense = { showQuickExpenseSheet = true },
                            onOpenAdjustLimit = { showAdjustLimitDialog = true },
                            onOpenCoachChat = { selectedTab = NavigationTab.AI_COACH },
                            onRefreshNudge = { viewModel.generateAiCoachAdvice() },
                            onContributeGoal = { targetGoalForContribution = it },
                            onDeleteGoal = { id -> viewModel.deleteGoal(id) },
                            onToggleTask = { id, comp -> viewModel.toggleTask(id, comp) },
                            onTriggerTaskNotification = { task -> viewModel.triggerTaskNotification(task) },
                            onDeleteTask = { id -> viewModel.deleteTask(id) },
                            onNavigateToGoals = { selectedTab = NavigationTab.GOALS },
                            onNavigateToReminders = { selectedTab = NavigationTab.SCHEDULE },
                            onNavigateToFinance = { selectedTab = NavigationTab.FINANCE },
                            onOpenProfileAuth = { showProfileAuthDialog = true }
                        )
                    }

                    NavigationTab.GOALS -> {
                        GoalsScreen(
                            goals = allGoals,
                            currencySymbol = currencySymbol,
                            onContribute = { gId, amt -> viewModel.contributeToGoal(gId, amt) },
                            onCreateGoal = { title, target, current, cat, deadline, color, icon, desc ->
                                viewModel.createGoal(title, target, current, cat, deadline, color, icon, desc)
                            },
                            onDeleteGoal = { id -> viewModel.deleteGoal(id) }
                        )
                    }

                    NavigationTab.FINANCE -> {
                        FinanceScreen(
                            transactions = allTransactions,
                            todaySpent = todaySpent ?: 0.0,
                            weekSpent = weekSpent ?: 0.0,
                            dailyLimit = dailyLimit,
                            currencySymbol = currencySymbol,
                            onOpenQuickExpense = { showQuickExpenseSheet = true },
                            onOpenAdjustLimit = { showAdjustLimitDialog = true },
                            onDeleteTransaction = { id -> viewModel.deleteExpense(id) }
                        )
                    }

                    NavigationTab.SCHEDULE -> {
                        RemindersScreen(
                            tasks = allTasks,
                            onToggleCompleted = { id, comp -> viewModel.toggleTask(id, comp) },
                            onTriggerNotification = { task -> viewModel.triggerTaskNotification(task) },
                            onAddTask = { title, desc, time, cat, prio, source ->
                                viewModel.addTask(title, desc, time, cat, prio, source)
                            },
                            onDeleteTask = { id -> viewModel.deleteTask(id) }
                        )
                    }

                    NavigationTab.AI_COACH -> {
                        AiCoachScreen(
                            messages = allCoachMessages,
                            isGenerating = isGeneratingCoach,
                            todaySpent = todaySpent ?: 0.0,
                            dailyLimit = dailyLimit,
                            goals = allGoals,
                            currencySymbol = currencySymbol,
                            onSendMessage = { query -> viewModel.generateAiCoachAdvice(query) }
                        )
                    }
                }
            }

            // Quick Expense Bottom Sheet Modal
            if (showQuickExpenseSheet) {
                QuickExpenseSheet(
                    currencySymbol = currencySymbol,
                    goals = allGoals,
                    onDismiss = { showQuickExpenseSheet = false },
                    onAddExpense = { title, amount, category, note, goalId ->
                        viewModel.addExpense(title, amount, category, note, goalId)
                    }
                )
            }

            // Adjust Daily Cap Dialog
            if (showAdjustLimitDialog) {
                AdjustLimitDialog(
                    currentLimit = dailyLimit,
                    currencySymbol = currencySymbol,
                    onDismiss = { showAdjustLimitDialog = false },
                    onConfirmLimit = { newLimit ->
                        viewModel.updateDailyLimit(newLimit)
                        showAdjustLimitDialog = false
                    }
                )
            }

            // Profile & PIN Security Dialog
            if (showProfileAuthDialog) {
                ProfileAuthDialog(
                    userSettings = userSettings,
                    onDismiss = { showProfileAuthDialog = false },
                    onSaveSettings = { updated ->
                        viewModel.updateSettings(updated)
                    },
                    onLockApp = {
                        isAppUnlocked = false
                        showProfileAuthDialog = false
                    }
                )
            }

            // Deposit to Goal dialog
            targetGoalForContribution?.let { goal ->
                DepositDialog(
                    goal = goal,
                    currencySymbol = currencySymbol,
                    onDismiss = { targetGoalForContribution = null },
                    onConfirmDeposit = { amount ->
                        viewModel.contributeToGoal(goal.id, amount)
                        targetGoalForContribution = null
                    }
                )
            }
        }
    }
}
