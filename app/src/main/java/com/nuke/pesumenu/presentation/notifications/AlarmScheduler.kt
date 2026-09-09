package com.nuke.pesumenu.presentation.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object AlarmScheduler {

    private const val BREAKFAST_ID = 1001
    private const val LUNCH_ID = 1002
    private const val SNACKS_ID = 1003
    private const val DINNER_1_ID = 1004
    private const val DINNER_2_ID = 1005

    fun scheduleAll(context: Context) {

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            return
        }

        scheduleMeal(
            context = context,
            alarmManager = alarmManager,
            hour = 7,
            minute = 40,
            mealType = 1,
            notificationId = BREAKFAST_ID
        )

        scheduleMeal(
            context = context,
            alarmManager = alarmManager,
            hour = 13,
            minute = 0,
            mealType = 2,
            notificationId = LUNCH_ID
        )

        scheduleMeal(
            context = context,
            alarmManager = alarmManager,
            hour = 16,
            minute = 45,
            mealType = 3,
            notificationId = SNACKS_ID
        )

        scheduleMeal(
            context = context,
            alarmManager = alarmManager,
            hour = 19,
            minute = 0,
            mealType = 4,
            notificationId = DINNER_1_ID
        )

        scheduleMeal(
            context = context,
            alarmManager = alarmManager,
            hour = 19,
            minute = 30,
            mealType = 4,
            notificationId = DINNER_2_ID
        )
    }

    private fun scheduleMeal(
        context: Context,
        alarmManager: AlarmManager,
        hour: Int,
        minute: Int,
        mealType: Int,
        notificationId: Int
    ) {

        val intent = Intent(
            context,
            MealNotificationReceiver::class.java
        ).apply {
            putExtra("mealType", mealType)
            putExtra("notificationId", notificationId)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val calendar = Calendar.getInstance().apply {

            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}