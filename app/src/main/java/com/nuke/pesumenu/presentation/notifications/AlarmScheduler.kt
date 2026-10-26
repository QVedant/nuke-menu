package com.nuke.pesumenu.presentation.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.nuke.pesumenu.presentation.settings.AppPreferences
import java.util.Calendar

object AlarmScheduler {

    fun scheduleAll(context: Context) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            return
        }

        val preferences = AppPreferences(context)

        ReminderConfig.ALL.forEach { config ->
            val (hour, minute) = preferences.getReminderTime(config)
            scheduleMeal(
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

    fun scheduleMeal(
        context: Context,
        alarmManager: AlarmManager,
        hour: Int,
        minute: Int,
        mealType: Int,
        notificationId: Int,
        isTomorrow: Boolean = false
    ) {
        val intent = Intent(
            context,
            MealNotificationReceiver::class.java
        ).apply {
            putExtra("mealType", mealType)
            putExtra("notificationId", notificationId)
            putExtra("isTomorrow", isTomorrow)
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
