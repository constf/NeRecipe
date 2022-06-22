package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipeRepository {
    val data: LiveData<List<Recipe>>
    val allData: LiveData<List<Recipe>>

    fun save(recipe: Recipe): Long
    fun remove(id: Long)
    fun setFavourite(id: Long, favourite: Boolean)
    fun getRecipeById(getId: Long): Recipe
}