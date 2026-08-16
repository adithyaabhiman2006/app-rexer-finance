package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.CoachMessageDao
import com.example.data.local.dao.GoalDao
import com.example.data.local.dao.ReminderTaskDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserSettingsDao
import com.example.data.local.entity.CoachMessageEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.ReminderTaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        GoalEntity::class,
        ReminderTaskEntity::class,
        CoachMessageEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun reminderTaskDao(): ReminderTaskDao
    abstract fun coachMessageDao(): CoachMessageDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabaseInstanceOnly(): AppDatabase? = INSTANCE

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rexer_hub_db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userSettingsDao = database.userSettingsDao()
            val goalDao = database.goalDao()
            val reminderDao = database.reminderTaskDao()
            val transactionDao = database.transactionDao()
            val coachDao = database.coachMessageDao()

            // 1. Initial User Settings
            userSettingsDao.insertOrUpdate(
                UserSettingsEntity(
                    id = 1,
                    userName = "REXER",
                    userRole = "Senior Software Engineer & Creator",
                    dailyBudgetLimit = 3000.0,
                    monthlyBudgetLimit = 90000.0,
                    currencySymbol = "₹",
                    pinCode = "1234",
                    isPinAuthEnabled = false,
                    isAppLocked = false,
                    totalSavingsSaved = 14500.0
                )
            )

            // 2. Dedicated Goals requested
            val initialGoals = listOf(
                GoalEntity(
                    title = "R15 V4 Modifications",
                    targetAmount = 35000.0,
                    currentAmount = 16500.0,
                    category = "Vehicle",
                    deadline = "2026-10-30",
                    colorHex = "#FF2A4B",
                    iconType = "bike",
                    description = "Quickshifter, Carbon Winglets, Akrapovič Slip-on & Custom Stealth Decal"
                ),
                GoalEntity(
                    title = "XAU/USD Trading Capital",
                    targetAmount = 100000.0,
                    currentAmount = 45000.0,
                    category = "Trading",
                    deadline = "2026-12-15",
                    colorHex = "#00E5FF",
                    iconType = "chart",
                    description = "Prop Firm Funded Account Reserve & Gold Scalping Liquidity"
                ),
                GoalEntity(
                    title = "REXER Brand Expansion",
                    targetAmount = 75000.0,
                    currentAmount = 32000.0,
                    category = "Brand",
                    deadline = "2026-11-20",
                    colorHex = "#FFB300",
                    iconType = "brand",
                    description = "Studio RGB Keylights, Shure SM7B Mic Setup & SaaS Infrastructure"
                )
            )
            goalDao.insertAll(initialGoals)

            // 3. Smart Reminders requested
            val initialReminders = listOf(
                ReminderTaskEntity(
                    title = "Save 500 Rs today!",
                    description = "Micro-savings allocation towards XAU/USD trading capital buffer.",
                    scheduledTime = "08:30 PM",
                    category = "Finance",
                    priority = "HIGH",
                    isCompleted = false,
                    isDailyPushEnabled = true,
                    integrationSource = "Google Tasks"
                ),
                ReminderTaskEntity(
                    title = "Check MetaTrader session timings",
                    description = "London & New York market overlap (1:30 PM - 5:30 PM UTC). High Gold volatility setup.",
                    scheduledTime = "01:30 PM",
                    category = "Trading",
                    priority = "HIGH",
                    isCompleted = false,
                    isDailyPushEnabled = true,
                    integrationSource = "Google Calendar"
                ),
                ReminderTaskEntity(
                    title = "Review Figma UI for client",
                    description = "Audit micro-interactions, dark mode tokens and export React component spec.",
                    scheduledTime = "04:00 PM",
                    category = "Figma & UI",
                    priority = "HIGH",
                    isCompleted = false,
                    isDailyPushEnabled = true,
                    integrationSource = "Google Tasks"
                ),
                ReminderTaskEntity(
                    title = "Dev Sprint: Deploy backend worker updates",
                    description = "Verify serverless API route latency and cache headers.",
                    scheduledTime = "11:00 AM",
                    category = "Dev & Code",
                    priority = "MEDIUM",
                    isCompleted = true,
                    isDailyPushEnabled = false,
                    integrationSource = "Google Calendar"
                )
            )
            reminderDao.insertAll(initialReminders)

            // 4. Initial Sample Transactions for today & past days to drive dial gauge
            val now = System.currentTimeMillis()
            val hourMillis = 3600 * 1000L
            val dayMillis = 24 * 3600 * 1000L

            transactionDao.insertTransaction(
                TransactionEntity(
                    title = "Espresso & Nutrition Fuel",
                    amount = 320.0,
                    category = "Food & Nutrition",
                    timestamp = now - (2 * hourMillis),
                    note = "Pre-coding session coffee"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    title = "AWS Serverless / Cloud Hosting",
                    amount = 650.0,
                    category = "Server & Cloud",
                    timestamp = now - (5 * hourMillis),
                    note = "Monthly sandbox instance"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    title = "R15 V4 High-Octane Fuel",
                    amount = 480.0,
                    category = "Bike & Transport",
                    timestamp = now - (8 * hourMillis),
                    note = "Full tank top-up"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    title = "Mechanical Switch Keycaps & Lube",
                    amount = 1200.0,
                    category = "Tech Gear & Setup",
                    timestamp = now - (1 * dayMillis),
                    note = "Custom split keyboard"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    title = "TradingView Premium Tier",
                    amount = 1450.0,
                    category = "Trading & Subs",
                    timestamp = now - (2 * dayMillis),
                    note = "Multi-chart layout subscription"
                )
            )

            // 5. Initial AI Coach Nudge
            coachDao.insertMessage(
                CoachMessageEntity(
                    sender = "coach",
                    content = "REXER, you have spent ₹1,450 out of your ₹3,000 daily budget (48%). Your spending pace is optimal. Keep your micro-saving discipline alive to reach your ₹35,000 R15 V4 target by October!",
                    timestamp = now,
                    type = "nudge"
                )
            )
        }
    }
}
