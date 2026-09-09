package com.nuke.pesumenu.presentation.data

import androidx.room3.Entity

@Entity(
    tableName = "meals",
    primaryKeys = ["week", "day", "mealType"]
)
data class MealEntity(
    val week: Int,
    val day: Int,
    val mealType: Int,
    val defaultMenu: String,
    val customMenu: String? = null
)