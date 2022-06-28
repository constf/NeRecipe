package ru.netology.nerecipe

import android.app.Application
import org.junit.Test

import org.junit.Assert.*
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.RecipesViewModel

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

class FilterTests {
    @Test
    fun createRecipe(){
        val viewModel = RecipesViewModel(Application())
        viewModel.initCategories()

        val recipe1 = Recipe(0L, "Recipe 1", "Me", 1L, false)
        val recipe2 = Recipe(0L, "Recipe 2", "Me", 2L, false)
        val recipe3 = Recipe(0L, "Recipe 3", "Me", 3L, true)
        val recipe4 = Recipe(0L, "Recipe 4", "Me", 3L, true)

        val id1 = viewModel.saveRecipe(recipe1)
        val id2 = viewModel.saveRecipe(recipe2)
        val id3 = viewModel.saveRecipe(recipe3)
        val id4 = viewModel.saveRecipe(recipe4)

        assertEquals(4, viewModel.allRecipesData.value?.size)
    }
}