package com.nuke.pesumenu.presentation.notifications

data class ReminderConfig(
    val id: Int,
    val key: String,
    val title: String,
    val defaultHour: Int,
    val defaultMinute: Int,
    val mealType: Int,
    val isTomorrow: Boolean = false
) {
    companion object {
        val BREAKFAST = ReminderConfig(
            id = 1001,
            key = "reminder_breakfast",
            title = "Breakfast",
            defaultHour = 7,
            defaultMinute = 40,
            mealType = 1
        )

        val LUNCH = ReminderConfig(
            id = 1002,
            key = "reminder_lunch",
            title = "Lunch",
            defaultHour = 12,
            defaultMinute = 25,
            mealType = 2
        )

        val SNACKS = ReminderConfig(
            id = 1003,
            key = "reminder_snacks",
            title = "Snacks",
            defaultHour = 16,
            defaultMinute = 45,
            mealType = 3
        )

        val DINNER = ReminderConfig(
            id = 1004,
            key = "reminder_dinner",
            title = "Dinner",
            defaultHour = 19,
            defaultMinute = 0,
            mealType = 4
        )

        val TOMORROW_BREAKFAST = ReminderConfig(
            id = 1005,
            key = "reminder_tomorrow_breakfast",
            title = "Sleep or Nah?",
            defaultHour = 21,
            defaultMinute = 0,
            mealType = 1,
            isTomorrow = true
        )

        val ALL = listOf(
            BREAKFAST,
            LUNCH,
            SNACKS,
            DINNER,
            TOMORROW_BREAKFAST
        )

        fun formatTime12Hour(hour: Int, minute: Int): String {
            val isPm = hour >= 12
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val minStr = minute.toString().padStart(2, '0')
            val amPm = if (isPm) "PM" else "AM"
            return "$hour12:$minStr $amPm"
        }

        fun formatTime24Hour(hour: Int, minute: Int): String {
            val hrStr = hour.toString().padStart(2, '0')
            val minStr = minute.toString().padStart(2, '0')
            return "$hrStr:$minStr"
        }
    }
}
