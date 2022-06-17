package ru.netology.nerecipe.viewModel

import ru.netology.nerecipe.dto.RecipeStep

interface StepsDetailsHelper {
    fun getResId(name: String): Int
    fun deleteEditedStep(step: RecipeStep)
    fun onChoosePictureClicked(step: RecipeStep)
    fun onEditStepContents(stepId: Long, text: String)
    fun updateStep(step: RecipeStep)
    fun editStep(step: RecipeStep)
}