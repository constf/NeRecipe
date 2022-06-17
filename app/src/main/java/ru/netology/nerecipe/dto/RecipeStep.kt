package ru.netology.nerecipe.dto

import android.net.Uri

data class RecipeStep(
    val id: Long,
    val recipeId: Long,
    val content: String,
    val picture: String = "empty",
    val pUri: Uri? = null
)
