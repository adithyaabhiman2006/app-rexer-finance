package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DepositDialog(
    goal: GoalEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirmDeposit: (Double) -> Unit
) {
    var depositAmountText by remember { mutableStateOf("1000") }
    val quickPicks = listOf(200, 500, 1000, 2500, 5000)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "Deposit to ${goal.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Allocate financial savings to speed up this milestone.",
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
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickPicks) { pick ->
                        Surface(
                            onClick = { depositAmountText = pick.toString() },
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Text(
                                text = "+$currencySymbol$pick",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirm Deposit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
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
    val presets = listOf(1500, 2000, 3000, 4500, 6000, 10000)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = NeonRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configure Daily Spending Limit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column {
                Text(
                    text = "Adjust the 24-hour spending ceiling for your central circular gauge.",
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
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("PRESET TARGETS", style = MaterialTheme.typography.labelMedium, fontSize = 10.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presets) { preset ->
                        Surface(
                            onClick = { limitInput = preset.toString() },
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceElevated,
                            border = BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Text(
                                text = "$currencySymbol$preset",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
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
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Daily Cap", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
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
    var userRole by remember { mutableStateOf(userSettings?.userRole ?: "Software Engineer & Content Creator") }
    var pinAuthEnabled by remember { mutableStateOf(userSettings?.isPinAuthEnabled ?: false) }
    var pinCode by remember { mutableStateOf(userSettings?.pinCode ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("REXER Profile & Security", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Profile Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = userRole,
                    onValueChange = { userRole = it },
                    label = { Text("Designation / Focus") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // PIN Security Row
                Surface(
                    shape = RoundedCornerShape(12.dp),
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
                                Text("Database & App Lock PIN", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Secure transaction & goal database", style = MaterialTheme.typography.bodyMedium, fontSize = 10.sp, color = TextMuted)
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
                        isPinAuthEnabled = pinAuthEnabled,
                        pinCode = pinCode
                    ) ?: UserSettingsEntity(
                        userName = userName.trim(),
                        userRole = userRole.trim(),
                        isPinAuthEnabled = pinAuthEnabled,
                        pinCode = pinCode
                    )
                    onSaveSettings(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Profile", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
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
