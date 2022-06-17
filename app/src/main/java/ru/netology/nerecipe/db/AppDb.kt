package ru.netology.nerecipe.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nerecipe.dao.*

@Database(entities = [RecCategoryEntity::class, RecipeEntity::class, RecStepEntity::class], version = 2)
abstract class AppDb: RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val recipeDao: RecipesDao
    abstract val recStepsDao: RecStepsDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDb {
            return Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}