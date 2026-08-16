package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CoachMessageEntity
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AiNudgeCard(
    nudge: CoachMessageEntity?,
    isGenerating: Boolean,
    onRefreshNudge: () -> Unit,
    onOpenCoachChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_nudge_card"),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, NeonRed.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            NeonRed.copy(alpha = 0.08f),
                            SurfaceDark
                        )
                    )
                )
                .padding(18.dp)
        ) {
            // Header: Gemini Sparkle + AI Coach Title + Refresh & Chat buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(NeonRed.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = NeonRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "AI PERSONAL COACH",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeonRed,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Gemini 3.5 Flash Intelligence",
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = NeonRed,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = onRefreshNudge,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Advice",
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        onClick = onOpenCoachChat,
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceElevated,
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Chat",
                                tint = TextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ask Coach",
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = 10.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body text / Nudge content
            val textContent = nudge?.content ?: "Analyzing your daily burndown pace, R15 V4 modification milestones, and MetaTrader session timings..."

            Text(
                text = textContent,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Prompt Suggestion Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = onOpenCoachChat,
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Text(
                        text = "💡 Fund R15 Faster",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 10.sp,
                        color = CyberCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    onClick = onOpenCoachChat,
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Text(
                        text = "📈 Trading Risk Plan",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 10.sp,
                        color = ElectricAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    onClick = onOpenCoachChat,
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Text(
                        text = "⚡ Sprint Focus",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 10.sp,
                        color = NeonRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
