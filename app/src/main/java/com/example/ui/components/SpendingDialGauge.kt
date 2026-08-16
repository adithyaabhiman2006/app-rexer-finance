package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedDark
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceBorderBright
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceHighlight
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
    val progress = if (dailyLimit > 0) (todaySpent / dailyLimit).toFloat().coerceIn(0f, 1.35f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "gauge_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val percent = if (dailyLimit > 0) ((todaySpent / dailyLimit) * 100).toInt() else 0
    val remaining = (dailyLimit - todaySpent).coerceAtLeast(0.0)

    val (statusLabel, statusColor, statusBg) = when {
        percent <= 50 -> Triple("BURNDOWN NOMINAL", EmeraldNeon, EmeraldNeon.copy(alpha = 0.12f))
        percent <= 80 -> Triple("OPTIMAL PACE", CyberCyan, CyberCyan.copy(alpha = 0.12f))
        percent <= 100 -> Triple("CRITICAL BUFFER", ElectricAmber, ElectricAmber.copy(alpha = 0.12f))
        else -> Triple("CAP BREACHED", NeonRed, NeonRed.copy(alpha = 0.16f))
    }

    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spending_dial_card"),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, SurfaceBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            statusColor.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(300f, 250f),
                        radius = 450f
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top control header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(20.dp))
                            .border(BorderStroke(1.dp, statusColor.copy(alpha = 0.35f)), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(statusColor.copy(alpha = pulseAlpha), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Surface(
                        onClick = onAdjustLimitClick,
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceElevated,
                        border = BorderStroke(1.dp, SurfaceBorder),
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
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Cap: $currencySymbol${formatter.format(dailyLimit.toInt())}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // High-Tech Cyber Speedometer Arc Instrument
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clickable { onAdjustLimitClick() }
                        .testTag("circular_dial_gauge"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawModernGaugeHUD(
                            progress = animatedProgress,
                            primaryColor = statusColor,
                            isOverLimit = percent > 100
                        )
                    }

                    // Floating Center Readout
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "TODAY'S OUTFLOW",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted,
                            fontSize = 9.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currencySymbol,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = formatter.format(todaySpent.toInt()),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        // Progress Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceHighlight.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, SurfaceBorderBright.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "used",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom telemetry metrics bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceElevated)
                        .border(BorderStroke(1.dp, SurfaceBorder.copy(alpha = 0.6f)), RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(CyberCyan, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AVAILABLE BUFFER",
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$currencySymbol${formatter.format(remaining.toInt())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (remaining > 0) EmeraldNeon else NeonRed
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(SurfaceBorder)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = ElectricAmber,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "TARGET CAP",
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$currencySymbol${formatter.format(dailyLimit.toInt())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas drawing for a sleek, clean, modern Gauge HUD
 */
private fun DrawScope.drawModernGaugeHUD(
    progress: Float,
    primaryColor: Color,
    isOverLimit: Boolean
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val outerRadius = size.width / 2f - 14f
    val innerRadius = outerRadius - 16f

    val startAngle = 145f
    val totalSweepAngle = 250f

    // 1. Draw Subtle Outer Tick Marks
    val totalTicks = 36
    for (i in 0..totalTicks) {
        val fraction = i.toFloat() / totalTicks
        val tickAngle = startAngle + (fraction * totalSweepAngle)
        val angleRad = (tickAngle * PI / 180f).toFloat()

        val isMajor = i % 6 == 0
        val tickLength = if (isMajor) 8f else 4f
        val tickStroke = if (isMajor) 2f else 1f

        val tickColor = if (fraction <= progress.coerceAtMost(1f)) {
            primaryColor.copy(alpha = if (isMajor) 0.8f else 0.4f)
        } else {
            SurfaceBorderBright.copy(alpha = if (isMajor) 0.4f else 0.15f)
        }

        val startX = center.x + (outerRadius + 8f - tickLength) * cos(angleRad)
        val startY = center.y + (outerRadius + 8f - tickLength) * sin(angleRad)
        val endX = center.x + (outerRadius + 8f) * cos(angleRad)
        val endY = center.y + (outerRadius + 8f) * sin(angleRad)

        drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = tickStroke,
            cap = StrokeCap.Round
        )
    }

    // 2. Background Track Arc
    drawArc(
        color = SurfaceElevated.copy(alpha = 0.9f),
        startAngle = startAngle,
        sweepAngle = totalSweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = 14f, cap = StrokeCap.Round)
    )

    // Inner Glow Shadow on Track
    drawArc(
        color = SurfaceBorder.copy(alpha = 0.5f),
        startAngle = startAngle,
        sweepAngle = totalSweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = 2f, cap = StrokeCap.Round)
    )

    // 3. Active Progress Arc with Gradient
    val clampedProgress = progress.coerceIn(0.01f, 1.25f)
    val activeSweep = (clampedProgress.coerceAtMost(1f)) * totalSweepAngle

    val arcGradient = Brush.sweepGradient(
        0.0f to CyberCyan,
        0.4f to EmeraldNeon,
        0.75f to ElectricAmber,
        1.0f to NeonRed,
        center = center
    )

    // Glow effect underneath
    drawArc(
        color = primaryColor.copy(alpha = 0.35f),
        startAngle = startAngle,
        sweepAngle = activeSweep,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = 20f, cap = StrokeCap.Round)
    )

    // Crisp foreground stroke
    drawArc(
        brush = arcGradient,
        startAngle = startAngle,
        sweepAngle = activeSweep,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = 14f, cap = StrokeCap.Round)
    )

    // 4. Glowing Head Bead (Current Indicator at the edge of the arc)
    val currentAngle = startAngle + activeSweep
    val currentAngleRad = (currentAngle * PI / 180f).toFloat()

    val beadX = center.x + outerRadius * cos(currentAngleRad)
    val beadY = center.y + outerRadius * sin(currentAngleRad)

    // Luminous halo
    drawCircle(
        color = primaryColor.copy(alpha = 0.4f),
        radius = 12f,
        center = Offset(beadX, beadY)
    )
    // White core
    drawCircle(
        color = Color.White,
        radius = 5f,
        center = Offset(beadX, beadY)
    )
}
