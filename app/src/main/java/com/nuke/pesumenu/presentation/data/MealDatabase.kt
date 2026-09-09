package com.nuke.pesumenu.presentation.data

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

@androidx.room3.Database(
    entities = [MealEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao

    companion object {

        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getInstance(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder<MealDatabase>(
                    context.applicationContext,
                    "pesu_menu.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}