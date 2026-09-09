package com.nuke.pesumenu.presentation.notifications

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

                val currentWeek =
                    preferences.getCurrentWeek()

                val today =
                    java.time.LocalDate.now()

                val dayNumber =
                    today.dayOfWeek.value % 7 + 1

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

                    val mealName =
                        when (mealType) {
                            1 -> "BREAKFAST"
                            2 -> "LUNCH"
                            3 -> "SNACKS"
                            4 -> "DINNER"
                            else -> "MEAL"
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

                // Schedule this reminder again for tomorrow.
                scheduleTomorrow(
                    context = context,
                    mealType = mealType,
                    notificationId = notificationId
                )

            } finally {

                pendingResult.finish()
            }
        }
    }

    private fun scheduleTomorrow(
        context: Context,
        mealType: Int,
        notificationId: Int
    ) {

        when (notificationId) {

            1001 -> schedule(
                context,
                7,
                40,
                mealType,
                notificationId
            )

            1002 -> schedule(
                context,
                13,
                0,
                mealType,
                notificationId
            )

            1003 -> schedule(
                context,
                16,
                45,
                mealType,
                notificationId
            )

            1004 -> schedule(
                context,
                19,
                0,
                mealType,
                notificationId
            )

            1005 -> schedule(
                context,
                19,
                30,
                mealType,
                notificationId
            )
        }
    }

    private fun schedule(
        context: Context,
        hour: Int,
        minute: Int,
        mealType: Int,
        notificationId: Int
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as android.app.AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            return
        }

        val intent =
            Intent(
                context,
                MealNotificationReceiver::class.java
            ).apply {
                putExtra("mealType", mealType)
                putExtra("notificationId", notificationId)
            }

        val pendingIntent =
            android.app.PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
            )

        val calendar =
            java.util.Calendar.getInstance().apply {

                add(
                    java.util.Calendar.DAY_OF_YEAR,
                    1
                )

                set(
                    java.util.Calendar.HOUR_OF_DAY,
                    hour
                )

                set(
                    java.util.Calendar.MINUTE,
                    minute
                )

                set(
                    java.util.Calendar.SECOND,
                    0
                )

                set(
                    java.util.Calendar.MILLISECOND,
                    0
                )
            }

        alarmManager.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}