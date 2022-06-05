package ru.netology.nerecipe.viewModel

import ru.netology.nerecipe.dto.RecipeStep

interface StepsDetailsHelper {
    fun getResId(name: String): Int
    fun deleteEditedStep(step: RecipeStep)
}