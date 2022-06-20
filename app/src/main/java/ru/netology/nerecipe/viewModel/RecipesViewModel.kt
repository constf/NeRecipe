package ru.netology.nerecipe.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

const val NEW_ITEM_ID = 0L

class RecipesViewModel(val inApplication: Application):
    AndroidViewModel(inApplication), RecipesFeederHelper, StepsDetailsHelper {

    //
    var isNewRecipe: Boolean = false
    var isNewStep: Boolean = false
    var selectedSpinner: String? = "empty"

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
    val showRecipe = SingleLiveEvent<Recipe?>()
    val editRecipe = SingleLiveEvent<Recipe?>()
    fun saveRecipe(recipe: Recipe) = recipesRepo.save(recipe)
    fun deleteRecipe(recipe: Recipe) = recipesRepo.remove(recipe.id)

    // Recipe steps data section
    private val recStepsRepo: RecipeStepsRepository =
        RecipeStepsRepositoryImplementation(AppDb.getInstance(inApplication).recStepsDao)
    val stepsFilteredData by recStepsRepo::dataFiltered
    val editedStepsList: MutableList<RecipeStep> = mutableListOf()
    val editStep = SingleLiveEvent<RecipeStep?>()
    private var editedStepsCount = 1L
    var stepUri: Uri? = null

    fun saveStep(step: RecipeStep) = recStepsRepo.save(step)
    fun removeStep(step: RecipeStep) = recStepsRepo.remove(step.id)

    fun clearEditedSteps() { editedStepsList.clear(); editedStepsCount = 1 }

    fun setEditedStepsPicture(picUri: Uri?) {
        // Basic checks
        val step = chooseThePicture.value ?: return
        if (picUri == null) return

        val stepId = step.id //This function is called from inside the observer of chooseThePicture

        stepUri = picUri

        // Extract the bitmap
        val inputStream = inApplication.contentResolver.openInputStream(picUri)
        val drawable = Drawable.createFromStream(inputStream, stepUri.toString())
        val bitmap = (drawable as BitmapDrawable).bitmap

        // arrange a file for it in drawable folder
        val timeMills = System.currentTimeMillis()
        val fileName: String = "picture_" + timeMills.toString() + ".jpg"
        // var file = inApplication.getDir("drawable", Context.MODE_PRIVATE)
        // file = File(file, fileName)
        val fos = inApplication.openFileOutput(fileName, Context.MODE_PRIVATE)

        // write bitmap to this file
        try {
            // val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        // save step with picture name
        saveStep(step.copy(picture = fileName))
    }


    // Events in UI
    val navigateToRecipeEditScreen = SingleLiveEvent<Unit>()
    val navigateToNewRecipeScreen = SingleLiveEvent<Unit>()
    val chooseThePicture = SingleLiveEvent<RecipeStep?>()



    override fun onChoosePictureClicked(step: RecipeStep) {
        chooseThePicture.value = step
    }

    override fun onEditStepContents(stepId: Long, text: String) {
        recStepsRepo.updateContent(stepId, text)
    }

    override fun updateStep(step: RecipeStep) {
        editedStepsList.add(step)
        recStepsRepo.updateStep(step)
    }

    override fun editStep(step: RecipeStep) {
        editStep.value = step
        isNewStep = false
    }

    override fun getBitmapFromFile(name: String): Bitmap? {
        val file = inApplication.filesDir.resolve(name)
        if ( !file.exists() ) return null
        val fis = inApplication.openFileInput(name)
        val bm = BitmapFactory.decodeStream(fis)
        fis.close()
        return bm
    }

    fun setRecipeNamesFilter(newText: String) {
        recipeNamesFilter.value = newText
    }

    fun clearShowRecipe() {
        showRecipe.value = null
    }

    fun addNewRecipe() {
        editedStepsList.clear()
        val newRecipe = Recipe(NEW_ITEM_ID, "", "", 1L, false)
        val recID = saveRecipe(newRecipe)

        editRecipe.value = newRecipe.copy(id = recID)
        isNewRecipe = true
    }

    fun onEditRecipe(recipe: Recipe) {
        editedStepsList.clear()
        editRecipe.value = recipe
        isNewRecipe = false
    }


    // Helper interface methods for use in RW adapter
    override fun onRecipeClicked(recipe: Recipe?) {
        showRecipe.value = recipe
    }
    override fun getCategoryName(id: Long) = getCatNameId(id)

    override fun getResId(name: String): Int {
        return inApplication.resources.getIdentifier(name, "drawable", inApplication.packageName)
    }



    fun addNewEditedStep() {
        val recId = editRecipe.value?.id ?: return
        val step: RecipeStep = RecipeStep(NEW_ITEM_ID, recId, "")
        isNewStep = true
        val stepId = saveStep(step)
        editStep.value = step.copy(id = stepId)
    }

    override fun deleteEditedStep(step: RecipeStep) {
        recStepsRepo.remove(step.id)
    }

    fun setFavourite(id: Long, favourite: Boolean) {
        recipesRepo.setFavourite(id, favourite)
    }

    fun onSaveEditedRecipe(recipe: Recipe) {
        val recId = saveRecipe(recipe)

        val stepsList = stepsFilteredData.value?.filter { it.recipeId == recId }

        stepsList?.forEach{ step ->
            updateStep(step)
        }

        editRecipe.value = null
    }

    fun getCatIdbyName(category: String?): Long? {
        return categoriesRepo.getIdByName(category)
    }

    fun getEditedRecipe(): Recipe? {
        return editRecipe.value
    }

    fun initCategories() {
        val cats = catData.value
        val size = cats?.size ?: 0
        if (size > 0) return

        saveCategory(RecCategory(NEW_ITEM_ID, "Please choose the category below:"))
        saveCategory(RecCategory(NEW_ITEM_ID, "Russian"))
        saveCategory(RecCategory(NEW_ITEM_ID, "International"))
        saveCategory(RecCategory(NEW_ITEM_ID, "Turkie"))
    }

    fun getEditedStep(): RecipeStep? {
        return editStep.value
    }

    fun onSaveEditedStep(newStep: RecipeStep) {
        saveStep(newStep)
        editStep.value = null
    }

    fun getRecipeById(id: Long): Recipe? {
        return recipesRepo.getRecipeById(id)
    }

    fun getStepById(stepId: Long): RecipeStep {
        return recStepsRepo.getStepById(stepId)
    }

}