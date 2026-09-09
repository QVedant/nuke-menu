package com.nuke.pesumenu.presentation.settings

import android.content.Context
import java.time.DayOfWeek
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

    fun getCurrentWeek(): Int {

        val startingWeek =
            preferences.getInt("starting_week", 1)

        val setupDate =
            LocalDate.parse(
                preferences.getString(
                    "setup_date",
                    LocalDate.now().toString()
                )
            )

        // Find the Sunday of the setup week
        val cycleStartDate =
            setupDate.minusDays(
                setupDate.dayOfWeek.value.toLong() % 7
            )

        val today = LocalDate.now()

        val daysSinceStart =
            java.time.temporal.ChronoUnit.DAYS.between(
                cycleStartDate,
                today
            )

        val weeksPassed =
            (daysSinceStart / 7).toInt()

        return ((startingWeek - 1 + weeksPassed) % 4) + 1
    }
}