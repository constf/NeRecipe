package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.RecipeStep

interface RecipeStepsRepository {
    val dataAll: LiveData<List<RecipeStep>>
    val dataFiltered: LiveData<List<RecipeStep>>

    fun save(step: RecipeStep): Long
    fun remove(id: Long)
    fun update(oldId: Long, newId: Long)

}