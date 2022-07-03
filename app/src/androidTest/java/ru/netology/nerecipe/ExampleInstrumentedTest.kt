package ru.netology.nerecipe

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.dto.RecipeStep
import ru.netology.nerecipe.viewModel.RecipesViewModel

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("ru.netology.nerecipe", appContext.packageName)
    }

    @Test
    fun createRecipe(){

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val viewModel = RecipesViewModel(appContext.applicationContext as Application)

        viewModel.initCategories()
        viewModel.clearAllRecipes()

        val recipe1 = Recipe(0L, "Recipe 1", "Me", 1L, false)
        val recipe2 = Recipe(0L, "Recipe 2", "Me", 2L, false)
        val recipe3 = Recipe(0L, "Recipe 3", "Me", 3L, true)
        val recipe4 = Recipe(0L, "Recipe 4", "Me", 3L, true)

        val id1 = viewModel.saveRecipe(recipe1)
        val id2 = viewModel.saveRecipe(recipe2)
        val id3 = viewModel.saveRecipe(recipe3)
        val id4 = viewModel.saveRecipe(recipe4)

        val listOfIds = listOf(id1, id2, id3, id4)
        val listRecipes1 = viewModel.getRecipesByIdList(listOfIds)
        val listRecipes2 = viewModel.listAllRecipes()

        assertEquals(4, listRecipes1.size)
        assertEquals(4, listRecipes2.size)
    }

    @Test
    fun createAndEditRecipes(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val viewModel = RecipesViewModel(appContext.applicationContext as Application)

        viewModel.initCategories()
        viewModel.clearAllRecipes()

        val recipe1 = Recipe(0L, "Recipe 1", "Me", 1L, false)
        val recipe2 = Recipe(0L, "Recipe 2", "Me", 2L, false)
        val recipe3 = Recipe(0L, "Recipe 3", "Me", 3L, true)
        val recipe4 = Recipe(0L, "Recipe 4", "Me", 3L, true)

        val id1 = viewModel.saveRecipe(recipe1)
        val id2 = viewModel.saveRecipe(recipe2)
        val id3 = viewModel.saveRecipe(recipe3)
        val id4 = viewModel.saveRecipe(recipe4)


        val step11 = RecipeStep(0L, id1,"Step 1 of recipe 1")
        val step12 = RecipeStep(0L, id1,"Step 2 of recipe 1")
        val step13 = RecipeStep(0L, id1,"Step 3 of recipe 1")

        val step21 = RecipeStep(0L, id2,"Step 1 of recipe 2")
        val step22 = RecipeStep(0L, id2,"Step 2 of recipe 2")

        val step31 = RecipeStep(0L, id3,"Step 1 of recipe 3")

        val step41 = RecipeStep(0L, id4,"Step 1 of recipe 4")
        val step42 = RecipeStep(0L, id4,"Step 2 of recipe 4")
        val step43 = RecipeStep(0L, id4,"Step 3 of recipe 4")
        val step44 = RecipeStep(0L, id4,"Step 4 of recipe 4")

        val id11 = viewModel.saveStep(step11)
        val id12 = viewModel.saveStep(step12)
        val id13 = viewModel.saveStep(step13)

        val id21 = viewModel.saveStep(step21)
        val id22 = viewModel.saveStep(step22)

        val id31 = viewModel.saveStep(step31)

        val id41 = viewModel.saveStep(step41)
        val id42 = viewModel.saveStep(step42)
        val id43 = viewModel.saveStep(step43)
        val id44 = viewModel.saveStep(step44)

        val listOfSteps1 = viewModel.getStepsWithRecId(id1)
        val listOfSteps2 = viewModel.getStepsWithRecId(id2)
        val listOfSteps3 = viewModel.getStepsWithRecId(id3)
        val listOfSteps4 = viewModel.getStepsWithRecId(id4)


        viewModel.removeStep(step44.copy(id = id44))
        val listOfSteps5 = viewModel.getStepsWithRecId(id4)


        assertEquals(3, listOfSteps1.size)
        assertEquals(2, listOfSteps2.size)
        assertEquals(1, listOfSteps3.size)
        assertEquals(4, listOfSteps4.size)
        assertEquals(3, listOfSteps5.size)
    }

    @Test
    fun deleteRecipes(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val viewModel = RecipesViewModel(appContext.applicationContext as Application)

        viewModel.initCategories()
        viewModel.clearAllRecipes()

        val recipe1 = Recipe(0L, "Recipe 1", "Me", 1L, false)
        val recipe2 = Recipe(0L, "Recipe 2", "Me", 2L, false)
        val recipe3 = Recipe(0L, "Recipe 3", "Me", 3L, true)
        val recipe4 = Recipe(0L, "Recipe 4", "Me", 3L, false)

        val id1 = viewModel.saveRecipe(recipe1)
        val id2 = viewModel.saveRecipe(recipe2)
        val id3 = viewModel.saveRecipe(recipe3)
        val id4 = viewModel.saveRecipe(recipe4)

        val listOfIds = listOf(id1, id2, id3, id4)
        val listRecipes1 = viewModel.getRecipesByIdList(listOfIds) // 4 recipes

        viewModel.deleteRecipe(recipe1.copy(id = id1))
        val listRecipes2 = viewModel.listAllRecipes() // 3 recipes

        viewModel.deleteRecipe(recipe2.copy(id = id2))
        val listRecipes3 = viewModel.listAllRecipes() // 2 recipes

        viewModel.deleteRecipe(recipe3)
        val listRecipes4 = viewModel.listAllRecipes() // 1 recipes


        assertEquals(4, listRecipes1.size)
        assertEquals(3, listRecipes2.size)
        assertEquals(2, listRecipes3.size)
        assertEquals(2, listRecipes4.size)
    }


    @Test
    fun categoriesSetAndRecipes() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val viewModel = RecipesViewModel(appContext.applicationContext as Application)

        viewModel.initCategories()
        viewModel.clearAllRecipes()

        val recipe1 = Recipe(0L, "Recipe 1", "Me", 1L, false)
        val recipe2 = Recipe(0L, "Recipe 2", "Me", 2L, true)
        val recipe3 = Recipe(0L, "Recipe 3", "Me", 3L, true)
        val recipe4 = Recipe(0L, "Recipe 4", "Me", 3L, false)

        val id1 = viewModel.saveRecipe(recipe1)
        val id2 = viewModel.saveRecipe(recipe2)
        val id3 = viewModel.saveRecipe(recipe3)
        val id4 = viewModel.saveRecipe(recipe4)
        viewModel.setCategoryVisible(1L)
        viewModel.setCategoryVisible(2L)
        viewModel.setCategoryVisible(3L)

        val listOfIds = listOf(id1, id2, id3, id4)
        val listRecipes1 = viewModel.listAllFilteredRecipes()

        viewModel.setCategoryInvisible(1L)
        val listRecipes2 = viewModel.listAllFilteredRecipes()

        viewModel.setCategoryInvisible(3L)
        val listRecipes3 = viewModel.listAllFilteredRecipes()

        viewModel.setCategoryVisible(1L)
        val listRecipes4 = viewModel.listAllFilteredRecipes()


        assertEquals(4, listRecipes1.size)
        assertEquals(3, listRecipes2.size)
        assertEquals(1, listRecipes3.size)
        assertEquals(2, listRecipes4.size)
    }


}