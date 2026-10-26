package com.nuke.pesumenu.presentation.settings

import android.content.Context
import com.nuke.pesumenu.presentation.notifications.ReminderConfig
import java.time.LocalDate

class AppPreferences(context: Context) {

    private val preferences =
        context.getSharedPreferences("canteen_preferences", Context.MODE_PRIVATE)

    fun isSetupComplete(): Boolean {
        return preferences.getBoolean("setup_complete", false)
    }

    fun saveWeekSetup(week: Int, setupDate: LocalDate) {
        preferences.edit()
            .putBoolean("setup_complete", true)
            .putInt("starting_week", week)
            .putString("setup_date", setupDate.toString())
            .apply()
    }

    fun getWeekForDate(targetDate: LocalDate): Int {
        val startingWeek =
            preferences.getInt("starting_week", 1)

        val setupDateStr =
            preferences.getString(
                "setup_date",
                targetDate.toString()
            ) ?: targetDate.toString()

        val setupDate = LocalDate.parse(setupDateStr)

        // Find the Sunday of the setup week
        val cycleStartDate =
            setupDate.minusDays(
                setupDate.dayOfWeek.value.toLong() % 7
            )

        val daysSinceStart =
            java.time.temporal.ChronoUnit.DAYS.between(
                cycleStartDate,
                targetDate
            )

        val weeksPassed =
            (daysSinceStart / 7).toInt()

        return ((startingWeek - 1 + weeksPassed) % 4) + 1
    }

    fun getCurrentWeek(): Int {
        return getWeekForDate(LocalDate.now())
    }

    fun getReminderTime(config: ReminderConfig): Pair<Int, Int> {
        val hour = preferences.getInt("${config.key}_hour", config.defaultHour)
        val minute = preferences.getInt("${config.key}_minute", config.defaultMinute)
        return Pair(hour, minute)
    }

    fun saveReminderTime(config: ReminderConfig, hour: Int, minute: Int) {
        preferences.edit()
            .putInt("${config.key}_hour", hour)
            .putInt("${config.key}_minute", minute)
            .apply()
    }

    fun resetAllReminderTimes() {
        val editor = preferences.edit()
        ReminderConfig.ALL.forEach { config ->
            editor.remove("${config.key}_hour")
            editor.remove("${config.key}_minute")
        }
        editor.apply()
    }
}
