package com.nuke.pesumenu.presentation.data

enum class Day {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY
}

enum class MealType {
    BREAKFAST,
    LUNCH,
    SNACKS,
    DINNER
}

data class Meal(
    val week: Int,
    val day: Day,
    val type: MealType,
    val menu: String
)