package ru.netology.nerecipe.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.dao.RecipesDao
import ru.netology.nerecipe.dao.toEntity
import ru.netology.nerecipe.dao.toModel
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.dto.Recipe

class RecipeRepositoryImplementation(private val dao: RecipesDao) : RecipeRepository {
    private var recipes: List<Recipe> = emptyList()
        get() = checkNotNull(data.value) { "Recipes data value should not be empty!" }

    override val data: LiveData<List<Recipe>> =
        dao.getAllFilteredRecipes().asLiveData().map { recList ->
            recList.map { it.toModel() }
        }

    override val allData: LiveData<List<Recipe>> = dao.getAllRecipes().asLiveData().map { recList ->
        recList.map { it.toModel() }
    }


    override fun save(recipe: Recipe): Long {
        return dao.insert(recipe.toEntity())
    }

    override fun remove(id: Long) {
        dao.removeById(id)
    }

    override fun clearAll() {
        dao.clearAll()
    }

    override fun getRecipesList(ids: List<Long>): List<Recipe> {
        return dao.getRecipesList(ids).map { it.toModel() }
    }

    override fun listAllRecipes(): List<Recipe> {
        return dao.listAllRecipes().map { it.toModel() }
    }


    override fun listAllSelectedRecipes(): List<Recipe> {
        return dao.listAllFilteredRecipes().map { it.toModel() }
    }

    override fun setFavourite(id: Long, favourite: Boolean) {
        dao.setFavourite(id, favourite)
    }

    override fun getRecipeById(getId: Long): Recipe {
        return dao.getRecipeById(getId).toModel()
    }

    override fun update(recipe: Recipe): Int {
        return dao.updateExistingRecipe(recipe.toEntity())
    }

    fun get(getId: Long): Recipe? {
        val rec = recipes.find { it.id == getId }
        return rec
    }
}