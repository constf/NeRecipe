package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipeRepository {
    val data: LiveData<List<Recipe>>
    val allData: LiveData<List<Recipe>>

    fun save(recipe: Recipe): Long
    fun remove(id: Long)
    fun clearAll()
    fun setFavourite(id: Long, favourite: Boolean)
    fun getRecipeById(getId: Long): Recipe
    fun update(recipe: Recipe): Int
    fun getRecipesList(ids: List<Long>): List<Recipe>
    fun listAllRecipes(): List<Recipe>
    fun listAllSelectedRecipes(): List<Recipe>
}