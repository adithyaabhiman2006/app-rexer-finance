package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.ReminderTaskEntity
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateCoachAnalysis(
        todaySpent: Double,
        dailyLimit: Double,
        weekSpent: Double,
        recentTransactions: List<TransactionEntity>,
        goals: List<GoalEntity>,
        pendingTasks: List<ReminderTaskEntity>,
        userCustomQuery: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = buildCoachPrompt(
            todaySpent = todaySpent,
            dailyLimit = dailyLimit,
            weekSpent = weekSpent,
            recentTransactions = recentTransactions,
            goals = goals,
            pendingTasks = pendingTasks,
            userCustomQuery = userCustomQuery
        )

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiCoach", "Using algorithmic smart coach fallback (no API key configured)")
            return@withContext generateLocalEngineNudge(
                todaySpent = todaySpent,
                dailyLimit = dailyLimit,
                weekSpent = weekSpent,
                goals = goals,
                pendingTasks = pendingTasks,
                userQuery = userCustomQuery
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val systemInstructionJson = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are Coach REXER, an elite personal AI financial strategist, productivity coach, and mindset mentor for a high-performance Software Engineer & Content Creator. Your style is razor-sharp, ultra-motivating, disciplined, modern, and pragmatic. Give concise, actionable advice (2-4 punchy paragraphs or bullet points with bold highlights). Mention exact numbers (e.g. daily limit burndown, R15 V4 modification progress, XAU/USD trading balance buffer, Figma UI client deliveries).")
                    })
                })
            }

            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("systemInstruction", systemInstructionJson)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("maxOutputTokens", 600)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
            }
            Log.w("GeminiCoach", "API returned non-successful response: ${response.code}, using smart local fallback")
            return@withContext generateLocalEngineNudge(
                todaySpent = todaySpent,
                dailyLimit = dailyLimit,
                weekSpent = weekSpent,
                goals = goals,
                pendingTasks = pendingTasks,
                userQuery = userCustomQuery
            )
        } catch (e: Exception) {
            Log.e("GeminiCoach", "Error invoking Gemini API: ${e.message}", e)
            return@withContext generateLocalEngineNudge(
                todaySpent = todaySpent,
                dailyLimit = dailyLimit,
                weekSpent = weekSpent,
                goals = goals,
                pendingTasks = pendingTasks,
                userQuery = userCustomQuery
            )
        }
    }

    private fun buildCoachPrompt(
        todaySpent: Double,
        dailyLimit: Double,
        weekSpent: Double,
        recentTransactions: List<TransactionEntity>,
        goals: List<GoalEntity>,
        pendingTasks: List<ReminderTaskEntity>,
        userCustomQuery: String?
    ): String {
        val txSummary = recentTransactions.take(5).joinToString(", ") { "${it.title}: ₹${it.amount} (${it.category})" }
        val goalSummary = goals.joinToString(", ") { "${it.title}: ₹${it.currentAmount}/₹${it.targetAmount} (${((it.currentAmount / it.targetAmount) * 100).toInt()}%)" }
        val taskSummary = pendingTasks.take(4).joinToString(", ") { "${it.title} [${it.category}]" }

        val spendRatio = if (dailyLimit > 0) (todaySpent / dailyLimit) * 100 else 0.0

        return if (!userCustomQuery.isNullOrBlank()) {
            """
            User Query: "$userCustomQuery"
            Current Metrics:
            - Today's Spending: ₹$todaySpent / Daily Limit: ₹$dailyLimit (${spendRatio.toInt()}%)
            - Weekly Spend: ₹$weekSpent
            - Goals: $goalSummary
            - Pending Tasks: $taskSummary
            - Recent Expenses: $txSummary
            Provide a direct, inspiring, and tactical answer for REXER.
            """.trimIndent()
        } else {
            """
            Analyze REXER's current daily financial & productivity status and give a high-impact daily nudge:
            - Today's Financial Burndown: ₹$todaySpent spent out of ₹$dailyLimit limit (${spendRatio.toInt()}%).
            - This Week's Total Burn: ₹$weekSpent.
            - Active Target Goals: $goalSummary.
            - Pending Smart Reminders & Schedule: $taskSummary.
            - Recent Transactions: $txSummary.

            Provide:
            1. **Daily Spending Gauge Audit**: Assessment of today's burndown pace and remaining buffer.
            2. **Goal Acceleration Strategy**: Micro-savings tip for R15 V4 Mods, XAU/USD Trading Capital, or Brand Expansion.
            3. **Focus & Execution Directive**: Immediate high-leverage task to crush next (e.g. MetaTrader session timing, Figma UI client review, or coding sprint).
            """.trimIndent()
        }
    }

    private fun generateLocalEngineNudge(
        todaySpent: Double,
        dailyLimit: Double,
        weekSpent: Double,
        goals: List<GoalEntity>,
        pendingTasks: List<ReminderTaskEntity>,
        userQuery: String?
    ): String {
        val spendPercent = if (dailyLimit > 0) ((todaySpent / dailyLimit) * 100).toInt() else 0
        val remaining = (dailyLimit - todaySpent).coerceAtLeast(0.0)

        if (!userQuery.isNullOrBlank()) {
            return when {
                userQuery.contains("r15", ignoreCase = true) || userQuery.contains("bike", ignoreCase = true) -> {
                    val r15Goal = goals.find { it.title.contains("R15", ignoreCase = true) }
                    val current = r15Goal?.currentAmount ?: 16500.0
                    val target = r15Goal?.targetAmount ?: 35000.0
                    val needed = (target - current).coerceAtLeast(0.0)
                    "🏍️ **R15 V4 Modification Roadmap**:\nYou have funded **₹${current.toInt()} / ₹${target.toInt()}** (${((current / target) * 100).toInt()}%). You only need **₹${needed.toInt()}** more! By saving ₹500/day from today's ₹${remaining.toInt()} unspent allowance, you will install the Quickshifter & Akrapovič Slip-on in just ${(needed / 500).toInt()} days!"
                }
                userQuery.contains("trade", ignoreCase = true) || userQuery.contains("xau", ignoreCase = true) || userQuery.contains("gold", ignoreCase = true) -> {
                    val xauGoal = goals.find { it.title.contains("XAU", ignoreCase = true) }
                    val current = xauGoal?.currentAmount ?: 45000.0
                    "📈 **XAU/USD Trading Capital Directive**:\nCurrent reserve: **₹${current.toInt()} / ₹100,000**. Remember: High risk-to-reward ratio wins the game. Respect the London-New York session overlap (1:30 PM UTC) and protect your capital from overtrading during low liquidity hours."
                }
                userQuery.contains("figma", ignoreCase = true) || userQuery.contains("client", ignoreCase = true) -> {
                    "🎨 **Client Delivery Execution**:\nAllocate an uninterrupted 45-minute deep work block for the Figma UI review. Finalize the dark-mode tokens and interactive prototypes before the client sync to ensure high retention."
                }
                else -> {
                    "⚡ **REXER Discipline Engine**:\nToday's spending gauge sits at **₹${todaySpent.toInt()} / ₹${dailyLimit.toInt()}** ($spendPercent%). You have **₹${remaining.toInt()}** buffer remaining. Stay dialed into your sprint priorities and maintain positive cashflow."
                }
            }
        }

        val statusText = when {
            spendPercent < 50 -> "🟢 **Pace: OPTIMAL BURNDOWN ($spendPercent%)** — You have a solid ₹${remaining.toInt()} safe buffer today."
            spendPercent < 85 -> "🟡 **Pace: MODERATE CONSUMPTION ($spendPercent%)** — Spend strictly on essential dev & nutrition."
            else -> "🔴 **Pace: CRITICAL LIMIT ALERT ($spendPercent%)** — Pause all non-essential discretionary purchases for today."
        }

        val r15Goal = goals.find { it.title.contains("R15", ignoreCase = true) }
        val r15Current = r15Goal?.currentAmount ?: 16500.0
        val r15Target = r15Goal?.targetAmount ?: 35000.0

        return """
        $statusText

        🎯 **Goal Velocity**:
        • **R15 V4 Modifications**: ₹${r15Current.toInt()}/₹${r15Target.toInt()} (${((r15Current / r15Target) * 100).toInt()}% funded).
        • **XAU/USD Capital**: Maintain trading risk management during today's overlap session.
        • **Savings Nudge**: Allocate ₹500 from your remaining ₹${remaining.toInt()} daily buffer into your capital reserves.

        🚀 **Execution Sprints**:
        Review your client Figma UI and deploy backend updates before market session open!
        """.trimIndent()
    }
}
