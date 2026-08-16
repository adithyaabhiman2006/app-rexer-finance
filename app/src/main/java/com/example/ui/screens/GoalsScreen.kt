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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.ui.components.DepositDialog
import com.example.ui.components.GoalCard
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalsScreen(
    goals: List<GoalEntity>,
    currencySymbol: String,
    onContribute: (Long, Double) -> Unit,
    onCreateGoal: (title: String, target: Double, current: Double, cat: String, deadline: String, color: String, icon: String, desc: String) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedGoalForDeposit by remember { mutableStateOf<GoalEntity?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Vehicle", "Trading", "Brand", "Hardware", "Career")
    val filteredGoals = if (selectedCategoryFilter == "All") {
        goals
    } else {
        goals.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.currentAmount }
    val overallProgress = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toInt() else 0

    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .testTag("goals_screen")
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
                            text = "TARGET GOALS",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Visual milestone tracking & dedicated capital reserves",
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
                        modifier = Modifier.testTag("create_goal_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Summary Stats Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "OVERALL TARGET PORTFOLIO",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberCyan.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "$overallProgress% FUNDED",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Accumulated", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                                Text(
                                    text = "$currencySymbol${formatter.format(totalSaved.toInt())}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Portfolio Target", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                                Text(
                                    text = "$currencySymbol${formatter.format(totalTarget.toInt())}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
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

            // Goal Cards List
            if (filteredGoals.isEmpty()) {
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
                            Icon(
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No target goals found in this category", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(filteredGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        currencySymbol = currencySymbol,
                        onContributeClick = { selectedGoalForDeposit = it },
                        onDeleteClick = onDeleteGoal
                    )
                }
            }
        }

        // Deposit Funds Modal Dialog
        selectedGoalForDeposit?.let { goal ->
            DepositDialog(
                goal = goal,
                currencySymbol = currencySymbol,
                onDismiss = { selectedGoalForDeposit = null },
                onConfirmDeposit = { amount ->
                    onContribute(goal.id, amount)
                    selectedGoalForDeposit = null
                }
            )
        }

        // Create Goal Dialog
        if (showCreateDialog) {
            CreateGoalDialog(
                currencySymbol = currencySymbol,
                onDismiss = { showCreateDialog = false },
                onCreate = { title, target, current, cat, deadline, color, icon, desc ->
                    onCreateGoal(title, target, current, cat, deadline, color, icon, desc)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateGoalDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onCreate: (title: String, target: Double, current: Double, cat: String, deadline: String, color: String, icon: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var currentText by remember { mutableStateOf("0") }
    var selectedCat by remember { mutableStateOf("Vehicle") }
    var description by remember { mutableStateOf("") }
    var iconType by remember { mutableStateOf("bike") }

    val categories = listOf("Vehicle", "Trading", "Brand", "Hardware", "Career")
    val icons = listOf("bike" to "🏍️ Bike", "chart" to "📈 Trading", "brand" to "🚀 Brand", "code" to "💻 Code", "star" to "⭐ Star")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Create New Target Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title (e.g. Custom Exhaust, 4K Monitor)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it },
                    label = { Text("Initial Saved Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Icon & Theme", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(icons) { (key, label) ->
                        val isSelected = iconType == key
                        Surface(
                            onClick = {
                                iconType = key
                                if (key == "bike") selectedCat = "Vehicle"
                                if (key == "chart") selectedCat = "Trading"
                                if (key == "brand") selectedCat = "Brand"
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonRed.copy(alpha = 0.2f) else SurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) NeonRed else SurfaceBorder)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) NeonRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Milestone Notes / Specifications") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: 0.0
                    val current = currentText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && target > 0) {
                        onCreate(title, target, current, selectedCat, "2026-12-31", "#FF2A4B", iconType, description)
                    }
                },
                enabled = title.isNotBlank() && (targetText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Create Goal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
