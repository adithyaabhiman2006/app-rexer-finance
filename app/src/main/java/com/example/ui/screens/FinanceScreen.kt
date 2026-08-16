package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceScreen(
    transactions: List<TransactionEntity>,
    todaySpent: Double,
    weekSpent: Double,
    dailyLimit: Double,
    currencySymbol: String,
    onOpenQuickExpense: () -> Unit,
    onOpenAdjustLimit: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Food & Nutrition", "Tech Gear & Setup", "Server & Cloud", "Trading & Subs", "Content & Studio", "Bike & Transport", "Lifestyle")

    val filteredTransactions = transactions.filter { tx ->
        val matchesCategory = (selectedCategory == "All" || tx.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = searchQuery.isBlank() || tx.title.contains(searchQuery, ignoreCase = true) || tx.note.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val totalSpentAllTime = transactions.sumOf { it.amount }
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .testTag("finance_screen")
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
                            text = "FINANCIAL BURNDOWN",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Daily burn rate, analytics & transaction audit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onOpenQuickExpense,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                        modifier = Modifier.testTag("finance_add_expense_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // High-Level Metric Tiles (Today, Week, Daily Limit)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Today
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("TODAY'S BURN", style = MaterialTheme.typography.labelMedium, fontSize = 9.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currencySymbol${formatter.format(todaySpent.toInt())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (todaySpent > dailyLimit) NeonRed else CyberCyan
                            )
                            Text(
                                text = "Cap: $currencySymbol${dailyLimit.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Week Total
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("WEEKLY BURN", style = MaterialTheme.typography.labelMedium, fontSize = 9.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currencySymbol${formatter.format(weekSpent.toInt())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ElectricAmber
                            )
                            Text(
                                text = "7-day rolling total",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Daily Cap Config Tile
                    Surface(
                        onClick = onOpenAdjustLimit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceElevated,
                        border = BorderStroke(1.dp, NeonRed.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("DAILY CAP", style = MaterialTheme.typography.labelMedium, fontSize = 9.sp, color = NeonRed)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = NeonRed, modifier = Modifier.size(10.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currencySymbol${formatter.format(dailyLimit.toInt())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap to change",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Weekly Spending Bar Chart (Custom Canvas)
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
                                text = "7-DAY BURNDOWN VELOCITY",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Daily Cap: $currencySymbol${dailyLimit.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonRed,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Canvas Bar Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                        ) {
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val mockSpends = listOf(1850f, 2900f, 1400f, 3200f, 2100f, 1600f, todaySpent.toFloat())

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val barWidth = (size.width / days.size) * 0.55f
                                val spacing = size.width / days.size
                                val maxVal = (mockSpends.maxOrNull() ?: 3000f).coerceAtLeast(dailyLimit.toFloat() * 1.1f)

                                // Draw limit threshold dashed line
                                val limitY = size.height - (size.height * (dailyLimit.toFloat() / maxVal))
                                drawLine(
                                    color = NeonRed.copy(alpha = 0.4f),
                                    start = Offset(0f, limitY),
                                    end = Offset(size.width, limitY),
                                    strokeWidth = 2f
                                )

                                mockSpends.forEachIndexed { index, spend ->
                                    val barHeight = (size.height * (spend / maxVal)).coerceAtLeast(8f)
                                    val startX = (index * spacing) + (spacing - barWidth) / 2f
                                    val startY = size.height - barHeight

                                    val isOver = spend > dailyLimit
                                    val isToday = index == days.size - 1

                                    val barColor = when {
                                        isOver -> NeonRed
                                        isToday -> CyberCyan
                                        else -> EmeraldNeon.copy(alpha = 0.7f)
                                    }

                                    drawRoundRect(
                                        color = barColor,
                                        topLeft = Offset(startX, startY),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Days labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today").forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 10.sp,
                                    color = if (day == "Today") CyberCyan else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Search and Category Filter
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search transactions...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonRed.copy(alpha = 0.2f) else SurfaceDark,
                            border = BorderStroke(1.dp, if (isSelected) NeonRed else SurfaceBorder)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Transactions List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTIONS HISTORY (${filteredTransactions.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
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
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions match your filter.", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(filteredTransactions, key = { it.id }) { tx ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SurfaceElevated
                                    ) {
                                        Text(
                                            text = tx.category,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontSize = 9.sp,
                                            color = CyberCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = dateFormat.format(Date(tx.timestamp)),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                if (tx.note.isNotBlank()) {
                                    Text(
                                        text = tx.note,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "-$currencySymbol${formatter.format(tx.amount.toInt())}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRed
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { onDeleteTransaction(tx.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
