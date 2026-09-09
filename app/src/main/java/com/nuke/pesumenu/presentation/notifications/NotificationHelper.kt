package com.nuke.pesumenu.presentation.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import androidx.core.content.ContextCompat

import com.nuke.pesumenu.presentation.MainActivity
import com.nuke.pesumenu.R

object NotificationHelper {

    const val CHANNEL_ID = "meal_reminders"

    fun createNotificationChannel(context: Context) {

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Reminders for PESU meals"
            }

        manager.createNotificationChannel(channel)
    }

    fun showMealNotification(
        context: Context,
        mealName: String,
        menu: String,
        notificationId: Int
    ) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            Notification.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_nuke_notification
                )
                .setContentTitle(
                    mealName
                )
                .setContentText(
                    menu
                )
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(menu)
                )
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(true)
                .setCategory(
                    Notification.CATEGORY_REMINDER
                )
                .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            notificationId,
            notification
        )
    }
}
