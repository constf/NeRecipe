package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.data.CategoryRepository
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.RecipeStepsRepository
import ru.netology.nerecipe.data.impl.CategoryRepositoryImplementation
import ru.netology.nerecipe.data.impl.RecipeRepositoryImplementation
import ru.netology.nerecipe.data.impl.RecipeStepsRepositoryImplementation
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.dto.RecCategory
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.dto.RecipeStep
import ru.netology.nmedia.util.SingleLiveEvent

const val NEW_ITEM_ID = 0L

class RecipesViewModel(val inApplication: Application):
    AndroidViewModel(inApplication), RecipesFeederHelper, StepsDetailsHelper {

    // Categories data section
    private val categoriesRepo: CategoryRepository =
        CategoryRepositoryImplementation(AppDb.getInstance(inApplication).categoryDao)
    val catData by categoriesRepo::data
    fun saveCategory(category: RecCategory) = categoriesRepo.save(category)
    fun getCatNameId(id: Long) = categoriesRepo.getName(id)

    // Recipes data section
    private val recipesRepo: RecipeRepository =
        RecipeRepositoryImplementation(AppDb.getInstance(inApplication).recipeDao)
    val recData by recipesRepo::data
    val recipeNamesFilter = SingleLiveEvent<String?>()
    fun saveRecipe(recipe: Recipe) = recipesRepo.save(recipe)
    fun deleteRecipe(recipe: Recipe) = recipesRepo.remove(recipe.id)

    // Recipe steps data section
    private val recStepsRepo: RecipeStepsRepository =
        RecipeStepsRepositoryImplementation(AppDb.getInstance(inApplication).recStepsDao)
    val stepsFilteredData by recStepsRepo::dataFiltered
    val editedStepsList: MutableLiveData<List<RecipeStep>?> = MutableLiveData(null)
    private var editedStepsCount = 1L

    fun saveStep(step: RecipeStep) = recStepsRepo.save(step)
    fun clearEditedSteps() { editedStepsList.value = null; editedStepsCount = 1 }



    // Events in UI
    val navigateToRecipeEditScreen = SingleLiveEvent<Unit>()
    val navigateToNewRecipeScreen = SingleLiveEvent<Unit>()
    val chooseThePicture = SingleLiveEvent<RecipeStep>()

    val showRecipe = SingleLiveEvent<Recipe?>()
    val editRecipe = SingleLiveEvent<Recipe?>()



    fun setRecipeNamesFilter(newText: String) {
        recipeNamesFilter.value = newText
    }

    fun clearRecipeNamesFilter() {
        recipeNamesFilter.value = null
    }

    fun clearShowRecipe() {
        showRecipe.value = null
    }

    fun addNewRecipe() {
        editRecipe.value = Recipe(NEW_ITEM_ID, "", "", NEW_ITEM_ID, false)
        navigateToNewRecipeScreen.call()
    }


    // Helper interface methods for use in RW adapter
    override fun onRecipeClicked(recipe: Recipe?) {
        showRecipe.value = recipe
    }
    override fun getCategoryName(id: Long) = getCatNameId(id)





    fun createInitialDataSet() {
        val cat1 = saveCategory(RecCategory(NEW_ITEM_ID, "Russian Kitchen", true))
        val cat2 = saveCategory(RecCategory(NEW_ITEM_ID, "International Kitchen", true))

        val rec1 = saveRecipe(Recipe(NEW_ITEM_ID, "Thin pancakes", "Konstantin", cat1, false))
        val rec2 = saveRecipe(Recipe(NEW_ITEM_ID, "Сырники", "Konstantin", cat1, false))
        val rec3 = saveRecipe(Recipe(NEW_ITEM_ID, "Cordon blue", "J.Biden", cat2, false))
        val rec4 = saveRecipe(Recipe(NEW_ITEM_ID, "French fries", "J.Biden", cat2, false))

        val step1 = saveStep(RecipeStep(NEW_ITEM_ID, rec1, "Prepare the powder, milk, eggs, water, solt, oil, butter, sugar."))
        val step2 = saveStep(RecipeStep(NEW_ITEM_ID, rec1, "Mix sugar with eggs and blend well."))
        val step3 = saveStep(RecipeStep(NEW_ITEM_ID, rec1, "Add milk and mix well and blend well."))
        val step4 = saveStep(RecipeStep(NEW_ITEM_ID, rec1, "Add powder and mix well. Then add the remaining ingredients."))
    }

    override fun getResId(name: String): Int {
        return inApplication.resources.getIdentifier(name, "drawable", inApplication.packageName)
    }



    fun addNewEditedStep() {
        val step: RecipeStep = RecipeStep(editedStepsCount++, NEW_ITEM_ID, "")
        val listSteps: MutableList<RecipeStep> = editedStepsList.value?.toMutableList() ?: mutableListOf<RecipeStep>()

        listSteps.add(step)

        editedStepsList.value = listSteps.toList()
    }

    override fun deleteEditedStep(step: RecipeStep) {
        val listSteps = editedStepsList.value ?: return
        val newList = listSteps.filter { it.id != step.id }
        editedStepsList.value = newList
    }

    fun setFavourite(id: Long, favourite: Boolean) {
        recipesRepo.setFavourite(id, favourite)
    }

}