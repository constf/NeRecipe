package ru.netology.nerecipe.viewModel

import android.graphics.Bitmap
import ru.netology.nerecipe.dto.RecipeStep

interface StepsDetailsHelper {
    fun deleteEditedStep(step: RecipeStep)
    fun onChoosePictureClicked(step: RecipeStep)
    fun onEditStepContents(stepId: Long, text: String)
    fun updateStep(step: RecipeStep)
    fun editStep(step: RecipeStep)
    fun getBitmapFromFile(name: String): Bitmap?
    fun updateStepsRepoWithListFromTo(list: List<RecipeStep>, dragFrom: Int, dragTo: Int)
}