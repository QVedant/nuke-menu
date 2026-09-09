package com.nuke.pesumenu.presentation.data

class MealRepository(
    private val dao: MealDao
) {

    suspend fun seedDatabaseIfEmpty() {

        val existingMeals = dao.getAllMeals()

        if (existingMeals.isNotEmpty()) {
            return
        }

        val meals = canteenMenu.map { meal ->

            MealEntity(
                week = meal.week,
                day = meal.day.ordinal + 1,
                mealType = meal.type.ordinal + 1,
                defaultMenu = meal.menu
            )
        }

        dao.insertAll(meals)
    }

    suspend fun getMealsForDay(
        week: Int,
        day: Int
    ): List<MealEntity> {
        return dao.getMealsForDay(week, day)
    }

    suspend fun updateCustomMenu(
        week: Int,
        day: Int,
        mealType: Int,
        customMenu: String
    ) {
        dao.updateCustomMenu(
            week,
            day,
            mealType,
            customMenu
        )
    }

    suspend fun resetToDefault(
        week: Int,
        day: Int,
        mealType: Int
    ) {
        dao.resetToDefault(
            week,
            day,
            mealType
        )
    }
}