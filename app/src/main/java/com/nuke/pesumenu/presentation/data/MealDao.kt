package com.nuke.pesumenu.presentation.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update

@Dao
interface MealDao {

    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<MealEntity>

    @Query("""
        SELECT * FROM meals
        WHERE week = :week
        AND day = :day
        ORDER BY mealType
    """)
    suspend fun getMealsForDay(
        week: Int,
        day: Int
    ): List<MealEntity>

    @Insert
    suspend fun insertAll(meals: List<MealEntity>)

    @Query("""
        UPDATE meals
        SET customMenu = :customMenu
        WHERE week = :week
        AND day = :day
        AND mealType = :mealType
    """)
    suspend fun updateCustomMenu(
        week: Int,
        day: Int,
        mealType: Int,
        customMenu: String?
    )

    @Query("""
        UPDATE meals
        SET customMenu = NULL
        WHERE week = :week
        AND day = :day
        AND mealType = :mealType
    """)
    suspend fun resetToDefault(
        week: Int,
        day: Int,
        mealType: Int
    )
}