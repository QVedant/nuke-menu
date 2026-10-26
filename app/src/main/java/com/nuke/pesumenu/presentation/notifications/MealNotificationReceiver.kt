package com.nuke.pesumenu.presentation.notifications

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nuke.pesumenu.presentation.data.MealDatabase
import com.nuke.pesumenu.presentation.data.MealRepository
import com.nuke.pesumenu.presentation.settings.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MealNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val pendingResult = goAsync()

        val mealType =
            intent.getIntExtra("mealType", 1)

        val notificationId =
            intent.getIntExtra("notificationId", 1001)

        val isTomorrow =
            intent.getBooleanExtra("isTomorrow", false)

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val repository =
                    MealRepository(
                        MealDatabase
                            .getInstance(context)
                            .mealDao()
                    )

                repository.seedDatabaseIfEmpty()

                val preferences =
                    AppPreferences(context)

                val targetDate = if (isTomorrow) {
                    java.time.LocalDate.now().plusDays(1)
                } else {
                    java.time.LocalDate.now()
                }

                val currentWeek =
                    preferences.getWeekForDate(targetDate)

                val dayNumber =
                    targetDate.dayOfWeek.value % 7 + 1

                val meals =
                    repository.getMealsForDay(
                        week = currentWeek,
                        day = dayNumber
                    )

                val meal =
                    meals.firstOrNull {
                        it.mealType == mealType
                    }

                if (meal != null) {

                    val mealName = if (isTomorrow) {
                        "SLEEP OR NAH? 😴"
                    } else {
                        when (mealType) {
                            1 -> "BREAKFAST"
                            2 -> "LUNCH"
                            3 -> "SNACKS"
                            4 -> "DINNER"
                            else -> "MEAL"
                        }
                    }

                    val menu =
                        meal.customMenu
                            ?: meal.defaultMenu

                    NotificationHelper.showMealNotification(
                        context = context,
                        mealName = mealName,
                        menu = menu,
                        notificationId = notificationId
                    )
                }

                // Schedule this reminder again for tomorrow using configured preference.
                scheduleTomorrow(
                    context = context,
                    notificationId = notificationId
                )

            } finally {

                pendingResult.finish()
            }
        }
    }

    private fun scheduleTomorrow(
        context: Context,
        notificationId: Int
    ) {
        val preferences = AppPreferences(context)
        val config = ReminderConfig.ALL.firstOrNull { it.id == notificationId } ?: return
        val (hour, minute) = preferences.getReminderTime(config)

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            return
        }

        AlarmScheduler.scheduleMeal(
            context = context,
            alarmManager = alarmManager,
            hour = hour,
            minute = minute,
            mealType = config.mealType,
            notificationId = config.id,
            isTomorrow = config.isTomorrow
        )
    }
}
