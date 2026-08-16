package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ReminderTaskEntity
import com.example.ui.components.ReminderItemCard
import com.example.ui.theme.CarbonBlack
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

@Composable
fun RemindersScreen(
    tasks: List<ReminderTaskEntity>,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onTriggerNotification: (ReminderTaskEntity) -> Unit,
    onAddTask: (title: String, desc: String, time: String, cat: String, priority: String, source: String) -> Unit,
    onDeleteTask: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Finance", "Trading", "Figma & UI", "Dev & Code")

    val filteredTasks = if (selectedCategoryFilter == "All") {
        tasks
    } else {
        tasks.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    val completedCount = tasks.count { it.isCompleted }
    val pendingCount = tasks.size - completedCount

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .testTag("reminders_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SMART REMINDERS",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Google Tasks & Calendar synchronized routines",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("create_task_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Alert", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sync Banner Card (Google Tasks & Google Calendar)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CyberCyan.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Google Tasks", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = CyberCyan)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ElectricAmber.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = ElectricAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Google Calendar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ElectricAmber)
                                    }
                                }
                            }

                            Text(
                                text = "ACTIVE ENGINE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Pending High-Focus Tasks", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                                Text(
                                    text = "$pendingCount Active",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pendingCount > 0) NeonRed else EmeraldNeon
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Completed Today", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                                Text(
                                    text = "$completedCount Done",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            onClick = { selectedCategoryFilter = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonRed.copy(alpha = 0.2f) else SurfaceDark,
                            border = BorderStroke(1.dp, if (isSelected) NeonRed else SurfaceBorder)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Reminders List
            if (filteredTasks.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No alerts found for this filter.", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    ReminderItemCard(
                        task = task,
                        onToggleCompleted = { comp -> onToggleCompleted(task.id, comp) },
                        onTriggerNotification = onTriggerNotification,
                        onDelete = onDeleteTask
                    )
                }
            }
        }

        // Add Reminder Dialog
        if (showCreateDialog) {
            CreateReminderDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { title, desc, time, cat, prio, source ->
                    onAddTask(title, desc, time, cat, prio, source)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateReminderDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, time: String, cat: String, prio: String, source: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00 AM") }
    var category by remember { mutableStateOf("Finance") }
    var priority by remember { mutableStateOf("HIGH") }
    var source by remember { mutableStateOf("Google Tasks") }

    val categories = listOf("Finance", "Trading", "Figma & UI", "Dev & Code", "Lifestyle")
    val priorities = listOf("HIGH", "MEDIUM", "LOW")
    val sources = listOf("Google Tasks", "Google Calendar", "REXER System")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("New Smart Reminder", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Save 500 Rs today!)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description / Strategy Details") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Scheduled Trigger Time (e.g. 01:30 PM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Integration Source", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sources) { s ->
                        val isSelected = source == s
                        Surface(
                            onClick = { source = s },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) CyberCyan else SurfaceBorder)
                        ) {
                            Text(
                                text = s,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) CyberCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text("Category", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isSelected = category == cat
                        Surface(
                            onClick = { category = cat },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonRed.copy(alpha = 0.2f) else SurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) NeonRed else SurfaceBorder)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) NeonRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text("Priority", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorities.forEach { prio ->
                        val isSelected = priority == prio
                        Surface(
                            onClick = { priority = prio },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonRed.copy(alpha = 0.2f) else SurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) NeonRed else SurfaceBorder)
                        ) {
                            Text(
                                text = prio,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, desc, time, category, priority, source)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Schedule Reminder", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
