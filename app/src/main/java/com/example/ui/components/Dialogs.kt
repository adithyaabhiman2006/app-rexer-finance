package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.UserSettingsEntity
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceBorderBright
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.CurrencyHelper
import com.example.util.CurrencyItem

/**
 * High-Speed Currency Switcher Dialog
 */
@Composable
fun CurrencyPickerDialog(
    currentCurrency: String,
    onDismiss: () -> Unit,
    onSelectCurrency: (String) -> Unit
) {
    var customCurrencyInput by remember { mutableStateOf("") }
    var selectedSymbol by remember { mutableStateOf(currentCurrency) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberCyan.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Change Active Currency",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Switch between $, LKR, ₹, €, £ or custom code",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SELECT GLOBAL / REGIONAL CURRENCY",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )

                // Grid of supported currencies
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(CurrencyHelper.supportedCurrencies) { item ->
                        val isSelected = selectedSymbol.equals(item.symbol, ignoreCase = true) ||
                                selectedSymbol.equals(item.code, ignoreCase = true)
                        Surface(
                            onClick = {
                                selectedSymbol = item.symbol
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonRed.copy(alpha = 0.2f) else SurfaceElevated,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NeonRed else SurfaceBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (item.flag.isNotEmpty()) {
                                        Text(text = item.flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Column {
                                        Text(
                                            text = item.symbol,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) NeonRed else TextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = item.code,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = NeonRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Currency Code/Symbol Input
                Text(
                    text = "OR ENTER CUSTOM CODE (E.G. RS, LKR, CHF, KRW)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )

                OutlinedTextField(
                    value = customCurrencyInput,
                    onValueChange = {
                        customCurrencyInput = it
                        if (it.isNotBlank()) selectedSymbol = it.trim()
                    },
                    placeholder = { Text("e.g. LKR or USD", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCurrency = if (customCurrencyInput.isNotBlank()) {
                        customCurrencyInput.trim()
                    } else {
                        selectedSymbol
                    }
                    onSelectCurrency(finalCurrency)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Apply Currency", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun DepositDialog(
    goal: GoalEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirmDeposit: (Double) -> Unit
) {
    val presets = CurrencyHelper.getGoalDepositPresets(currencySymbol)
    var depositAmountText by remember { mutableStateOf(presets.firstOrNull()?.toString() ?: "1000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(NeonRed.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Deposit to ${goal.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Milestone: ${goal.category.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Allocate savings from your capital reserves directly into this milestone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = depositAmountText,
                    onValueChange = { depositAmountText = it },
                    label = { Text("Deposit Amount ($currencySymbol)") },
                    prefix = { Text("$currencySymbol ", color = NeonRed, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "QUICK ALLOCATE PRESETS",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets) { pick ->
                        Surface(
                            onClick = { depositAmountText = pick.toString() },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Text(
                                text = "+$currencySymbol$pick",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = depositAmountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirmDeposit(amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm Deposit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AdjustLimitDialog(
    currentLimit: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirmLimit: (Double) -> Unit
) {
    var limitInput by remember { mutableStateOf(currentLimit.toInt().toString()) }
    val presets = CurrencyHelper.getDailyCapPresets(currencySymbol)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(NeonRed.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = NeonRed, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Daily Spending Cap", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Speedometer Gauge Ceiling", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Adjust the 24-hour spending ceiling for your central circular HUD gauge.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = limitInput,
                    onValueChange = { limitInput = it },
                    label = { Text("Daily Budget Limit ($currencySymbol)") },
                    prefix = { Text("$currencySymbol ", color = NeonRed, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adjust_limit_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("PRESET TARGETS", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presets) { preset ->
                        Surface(
                            onClick = { limitInput = preset.toString() },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Text(
                                text = "$currencySymbol$preset",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newLimit = limitInput.toDoubleOrNull() ?: currentLimit
                    if (newLimit > 0) {
                        onConfirmLimit(newLimit)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Daily Cap", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ProfileAuthDialog(
    userSettings: UserSettingsEntity?,
    onDismiss: () -> Unit,
    onSaveSettings: (UserSettingsEntity) -> Unit,
    onLockApp: () -> Unit
) {
    var userName by remember { mutableStateOf(userSettings?.userName ?: "REXER") }
    var userRole by remember { mutableStateOf(userSettings?.userRole ?: "Software Engineer & Creator") }
    var currencySymbol by remember { mutableStateOf(userSettings?.currencySymbol ?: "$") }
    var pinAuthEnabled by remember { mutableStateOf(userSettings?.isPinAuthEnabled ?: false) }
    var pinCode by remember { mutableStateOf(userSettings?.pinCode ?: "") }

    val quickCurrencies = listOf("$", "LKR", "₹", "€", "£", "¥", "A$", "C$", "AED")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberCyan.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("REXER Profile & Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("Identity, Security & Currency Preferences", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Profile Identity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated
                    )
                )

                OutlinedTextField(
                    value = userRole,
                    onValueChange = { userRole = it },
                    label = { Text("Focus / Designation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated
                    )
                )

                // Currency Selection Section
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE CURRENCY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Current: $currencySymbol",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonRed,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(quickCurrencies) { curr ->
                                val isSelected = currencySymbol == curr
                                Surface(
                                    onClick = { currencySymbol = curr },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) NeonRed else SurfaceDark,
                                    border = BorderStroke(1.dp, if (isSelected) NeonRed else SurfaceBorder)
                                ) {
                                    Text(
                                        text = curr,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it.trim() },
                            label = { Text("Custom Currency Symbol / Code", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark
                            )
                        )
                    }
                }

                // PIN Security Row
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("App Lock PIN", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Secure transaction & goal ledger", style = MaterialTheme.typography.bodyMedium, fontSize = 10.sp, color = TextMuted)
                            }
                            Switch(
                                checked = pinAuthEnabled,
                                onCheckedChange = { pinAuthEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonRed, checkedTrackColor = NeonRed.copy(alpha = 0.3f))
                            )
                        }

                        if (pinAuthEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { if (it.length <= 6) pinCode = it },
                                label = { Text("4-6 Digit Security PIN") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = userSettings?.copy(
                        userName = userName.trim(),
                        userRole = userRole.trim(),
                        currencySymbol = currencySymbol.trim().ifBlank { "$" },
                        isPinAuthEnabled = pinAuthEnabled,
                        pinCode = pinCode
                    ) ?: UserSettingsEntity(
                        userName = userName.trim(),
                        userRole = userRole.trim(),
                        currencySymbol = currencySymbol.trim().ifBlank { "$" },
                        isPinAuthEnabled = pinAuthEnabled,
                        pinCode = pinCode
                    )
                    onSaveSettings(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Profile", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun PinLockScreen(
    correctPin: String,
    onUnlockSuccess: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(NeonRed.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = NeonRed,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "REXER HUB SECURITY",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Enter PIN to access financial data & trading capital",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PIN Dots Display
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (isFilled) NeonRed else SurfaceElevated,
                                CircleShape
                            )
                            .border(1.dp, if (isFilled) NeonRed else SurfaceBorder, CircleShape)
                    )
                }
            }

            if (isError) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Incorrect PIN. Please try again.", color = NeonRed, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Numeric Keypad (1-9, 0, Backspace, Clear)
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "⌫")
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                keypad.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { digit ->
                            Surface(
                                onClick = {
                                    when (digit) {
                                        "C" -> {
                                            enteredPin = ""
                                            isError = false
                                        }
                                        "⌫" -> {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                                isError = false
                                            }
                                        }
                                        else -> {
                                            if (enteredPin.length < 6) {
                                                enteredPin += digit
                                                isError = false
                                                if (enteredPin == correctPin) {
                                                    onUnlockSuccess()
                                                } else if (enteredPin.length == correctPin.length && enteredPin != correctPin) {
                                                    isError = true
                                                    enteredPin = ""
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceElevated,
                                border = BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = digit,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (digit == "C" || digit == "⌫") CyberCyan else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick bypass for dev/testing
            OutlinedButton(
                onClick = onUnlockSuccess,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Bypass / Quick Unlock", color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}
