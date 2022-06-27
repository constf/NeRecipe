package ru.netology.nerecipe.viewModel

import ru.netology.nerecipe.dto.Recipe

interface RecipesFeederHelper {
    fun onRecipeClicked(recipe: Recipe?)
    fun getCategoryName(id: Long): String?
    fun onFavouriteClicked(recipe: Recipe?)
    fun updateRepoWithNewListFromTo(list: List<Recipe>, dragFrom: Int, dragTo: Int): Boolean
}