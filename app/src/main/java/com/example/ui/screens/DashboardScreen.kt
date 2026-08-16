package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CoachMessageEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.ReminderTaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserSettingsEntity
import com.example.ui.components.AiNudgeCard
import com.example.ui.components.GoalCard
import com.example.ui.components.ReminderItemCard
import com.example.ui.components.SpendingDialGauge
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    todaySpent: Double,
    dailyLimit: Double,
    currencySymbol: String,
    userSettings: UserSettingsEntity?,
    todayTransactions: List<TransactionEntity>,
    goals: List<GoalEntity>,
    tasks: List<ReminderTaskEntity>,
    latestNudge: CoachMessageEntity?,
    isGeneratingCoach: Boolean,
    onOpenQuickExpense: () -> Unit,
    onOpenAdjustLimit: () -> Unit,
    onOpenCoachChat: () -> Unit,
    onRefreshNudge: () -> Unit,
    onContributeGoal: (GoalEntity) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onTriggerTaskNotification: (ReminderTaskEntity) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onOpenProfileAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    val currentDateStr = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "REXER",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = NeonRed,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Text(
                                text = "HUB",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "$currentDateStr • ${userSettings?.userRole ?: "Engineer & Creator"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Profile & Security Lock Badge
                Surface(
                    onClick = onOpenProfileAuth,
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.testTag("profile_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(NeonRed.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (userSettings?.isPinAuthEnabled == true) Icons.Default.Lock else Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = NeonRed,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = userSettings?.userName ?: "REXER",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 1. Main Highlight: Circular Dial Layout with Perimeter Ticks and Red Indicator
        item {
            SpendingDialGauge(
                todaySpent = todaySpent,
                dailyLimit = dailyLimit,
                currencySymbol = currencySymbol,
                onAdjustLimitClick = onOpenAdjustLimit
            )
        }

        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Log Expense Button
                Button(
                    onClick = onOpenQuickExpense,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dashboard_log_expense_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Log Expense",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // AI Coach Nudge Button
                Surface(
                    onClick = onOpenCoachChat,
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dashboard_ask_coach_btn")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ask Coach",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. Gemini AI Personal Coach Daily Nudge Card
        item {
            AiNudgeCard(
                nudge = latestNudge,
                isGenerating = isGeneratingCoach,
                onRefreshNudge = onRefreshNudge,
                onOpenCoachChat = onOpenCoachChat
            )
        }

        // 3. Dedicated Goal Trackers Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TARGET GOALS",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "View All (${goals.size}) >",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan,
                    modifier = Modifier.clickable { onNavigateToGoals() }
                )
            }
        }

        items(goals.take(3), key = { it.id }) { goal ->
            GoalCard(
                goal = goal,
                currencySymbol = currencySymbol,
                onContributeClick = onContributeGoal,
                onDeleteClick = onDeleteGoal
            )
        }

        // 4. Smart Reminders & Schedule Section (Google Tasks & Calendar)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TODAY'S SCHEDULE & ALERTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Manage Tasks >",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan,
                    modifier = Modifier.clickable { onNavigateToReminders() }
                )
            }
        }

        items(tasks.take(3), key = { it.id }) { task ->
            ReminderItemCard(
                task = task,
                onToggleCompleted = { completed -> onToggleTask(task.id, completed) },
                onTriggerNotification = onTriggerTaskNotification,
                onDelete = onDeleteTask
            )
        }

        // 5. Today's Transactions Overview
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = ElectricAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TODAY'S LOGS (${todayTransactions.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "History >",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan,
                    modifier = Modifier.clickable { onNavigateToFinance() }
                )
            }
        }

        if (todayTransactions.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No expenses logged today yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onOpenQuickExpense,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Log First Expense", color = NeonRed)
                        }
                    }
                }
            }
        } else {
            items(todayTransactions.take(4), key = { it.id }) { tx ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tx.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${tx.category} ${if (tx.note.isNotBlank()) "• ${tx.note}" else ""}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "-$currencySymbol${formatter.format(tx.amount.toInt())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonRed
                        )
                    }
                }
            }
        }
    }
}
