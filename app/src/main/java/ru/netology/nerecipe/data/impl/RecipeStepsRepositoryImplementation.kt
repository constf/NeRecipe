package ru.netology.nerecipe.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.dao.RecStepsDao
import ru.netology.nerecipe.dao.toEntity
import ru.netology.nerecipe.dao.toModel
import ru.netology.nerecipe.data.RecipeStepsRepository
import ru.netology.nerecipe.dto.RecipeStep

class RecipeStepsRepositoryImplementation(private val dao: RecStepsDao): RecipeStepsRepository {

    private var stepsFiltered: List<RecipeStep> = emptyList()
        get() = checkNotNull(dataFiltered.value) {"Steps data value should not be empty!"}

    override val dataAll: LiveData<List<RecipeStep>> = dao.getAllRecipeSteps().asLiveData().map { stepsList ->
        stepsList.map { it.toModel() }
    }

    override val dataFiltered: LiveData<List<RecipeStep>> = dao.getFilteredRecipeSteps().asLiveData().map { filteredList ->
        filteredList.map { it.toModel() }
    }

    override fun getStepById(getId: Long): RecipeStep {
        return dao.getStepById(getId).toModel()
    }

    override fun save(step: RecipeStep): Long {
        return dao.insert(step.toEntity())
    }

    override fun remove(id: Long) {
        dao.removeById(id)
    }

    override fun update(oldId: Long, newId: Long) {
        dao.updateRecipesIds(oldId, newId)
    }

    fun get(getId: Long): RecipeStep? {
        val rs = stepsFiltered.find { it.id == getId }
        return rs
    }

    override fun updateContent(stepId: Long, newContent: String) {
        dao.updateStepContent(newContent, stepId)
    }

    override fun updateStep(step: RecipeStep) {
        dao.insert(step.toEntity())
    }

}