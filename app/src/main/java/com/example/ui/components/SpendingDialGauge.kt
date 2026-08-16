package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedDark
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpendingDialGauge(
    todaySpent: Double,
    dailyLimit: Double,
    currencySymbol: String = "₹",
    onAdjustLimitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (dailyLimit > 0) (todaySpent / dailyLimit).toFloat().coerceIn(0f, 1.5f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge_progress"
    )

    val percent = if (dailyLimit > 0) ((todaySpent / dailyLimit) * 100).toInt() else 0
    val remaining = (dailyLimit - todaySpent).coerceAtLeast(0.0)

    val (statusText, statusColor, statusBadgeBg) = when {
        percent <= 50 -> Triple("BURNDOWN SAFE", EmeraldNeon, EmeraldNeon.copy(alpha = 0.15f))
        percent <= 80 -> Triple("ACTIVE TRACKING", CyberCyan, CyberCyan.copy(alpha = 0.15f))
        percent <= 100 -> Triple("APPROACHING CAP", ElectricAmber, ElectricAmber.copy(alpha = 0.15f))
        else -> Triple("OVER LIMIT", NeonRed, NeonRed.copy(alpha = 0.15f))
    }

    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spending_dial_card"),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row with title & adjust limit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY SPENDING DIAL",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                }

                Surface(
                    onClick = onAdjustLimitClick,
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.testTag("adjust_limit_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Adjust Daily Limit",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Limit: $currencySymbol${formatter.format(dailyLimit.toInt())}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Circular Dial Gauge Layout with perimeter ticks and central red indicator
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clickable { onAdjustLimitClick() }
                    .testTag("circular_dial_gauge"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawDialGauge(
                        progress = animatedProgress,
                        progressPercent = percent
                    )
                }

                // Central Readout inside the dial
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBadgeBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Main Spent Amount Display
                    Text(
                        text = "$currencySymbol${formatter.format(todaySpent.toInt())}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "of $currencySymbol${formatter.format(dailyLimit.toInt())} ($percent%)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Remaining Buffer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (todaySpent <= dailyLimit) "Buffer: " else "Deficit: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "$currencySymbol${formatter.format(if (todaySpent <= dailyLimit) remaining.toInt() else (todaySpent - dailyLimit).toInt())}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (todaySpent <= dailyLimit) EmeraldNeon else NeonRed,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Footnote Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceElevated, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("SPENDING PACE", style = MaterialTheme.typography.labelMedium, fontSize = 9.sp, color = TextMuted)
                    Text("$percent% of 24h cap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(SurfaceBorder))

                Column(horizontalAlignment = Alignment.End) {
                    Text("REMAINING ALLOWANCE", style = MaterialTheme.typography.labelMedium, fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "$currencySymbol${formatter.format(remaining.toInt())}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (remaining > 0) CyberCyan else NeonRed
                    )
                }
            }
        }
    }
}

/**
 * Custom Canvas drawing for the Circular Dial Layout:
 * - Perimeter tick marks (major and minor graduations around 240-degree arc)
 * - Track arc with neon cyber gradient
 * - Central red needle indicator pointing to the current spending fraction
 * - Glowing center pivot
 */
private fun DrawScope.drawDialGauge(
    progress: Float,
    progressPercent: Int
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val outerRadius = size.width / 2f - 12f
    val innerRadius = outerRadius - 22f
    val tickOuterRadius = outerRadius + 6f

    val startAngle = 150f // Starts bottom-left
    val totalSweepAngle = 240f // Sweeps 240 degrees to bottom-right

    // 1. Draw Perimeter Graduated Tick Marks
    val totalTicks = 48
    for (i in 0..totalTicks) {
        val fraction = i.toFloat() / totalTicks
        val tickAngle = startAngle + (fraction * totalSweepAngle)
        val angleRad = (tickAngle * PI / 180f).toFloat()

        val isMajor = i % 6 == 0
        val isMedium = i % 3 == 0

        val tickLength = if (isMajor) 12f else if (isMedium) 8f else 5f
        val tickStroke = if (isMajor) 2.5f else if (isMedium) 1.5f else 1f

        val tickColor = when {
            fraction <= progress.coerceAtMost(1f) -> {
                if (fraction > 0.8f) NeonRed else if (fraction > 0.5f) ElectricAmber else CyberCyan
            }
            isMajor -> TextSecondary.copy(alpha = 0.4f)
            else -> TextMuted.copy(alpha = 0.2f)
        }

        val startX = center.x + (tickOuterRadius - tickLength) * cos(angleRad)
        val startY = center.y + (tickOuterRadius - tickLength) * sin(angleRad)
        val endX = center.x + tickOuterRadius * cos(angleRad)
        val endY = center.y + tickOuterRadius * sin(angleRad)

        drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = tickStroke,
            cap = StrokeCap.Round
        )
    }

    // 2. Draw Background Arc Track
    drawArc(
        color = SurfaceBorder.copy(alpha = 0.5f),
        startAngle = startAngle,
        sweepAngle = totalSweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius + 6f, center.y - outerRadius + 6f),
        size = Size((outerRadius - 6f) * 2, (outerRadius - 6f) * 2),
        style = Stroke(width = 12f, cap = StrokeCap.Round)
    )

    // 3. Draw Active Spending Progress Arc with Multi-Color Cyber Gradient
    val clampedProgress = progress.coerceIn(0.01f, 1f)
    val activeSweep = clampedProgress * totalSweepAngle

    val arcGradient = Brush.sweepGradient(
        0.0f to CyberCyan,
        0.5f to ElectricAmber,
        0.85f to NeonRed,
        1.0f to NeonRedDark,
        center = center
    )

    drawArc(
        brush = arcGradient,
        startAngle = startAngle,
        sweepAngle = activeSweep,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius + 6f, center.y - outerRadius + 6f),
        size = Size((outerRadius - 6f) * 2, (outerRadius - 6f) * 2),
        style = Stroke(width = 12f, cap = StrokeCap.Round)
    )

    // 4. Central Red Indicator Needle & Pivot
    val currentNeedleAngle = startAngle + (progress.coerceIn(0f, 1.2f) * totalSweepAngle)
    val needleAngleRad = (currentNeedleAngle * PI / 180f).toFloat()

    val needleLength = innerRadius - 16f
    val needleEndX = center.x + needleLength * cos(needleAngleRad)
    val needleEndY = center.y + needleLength * sin(needleAngleRad)

    // Red Needle Body
    drawLine(
        color = NeonRed,
        start = center,
        end = Offset(needleEndX, needleEndY),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    // Red Arrow / Tip Highlight
    drawCircle(
        color = NeonRed,
        radius = 5f,
        center = Offset(needleEndX, needleEndY)
    )

    // Central Glowing Pivot Hub
    drawCircle(
        color = NeonRed.copy(alpha = 0.25f),
        radius = 16f,
        center = center
    )
    drawCircle(
        color = NeonRedDark,
        radius = 10f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = 4f,
        center = center
    )
}
