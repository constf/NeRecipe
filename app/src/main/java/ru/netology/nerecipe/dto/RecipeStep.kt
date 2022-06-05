package ru.netology.nerecipe.dto

data class RecipeStep(
    val id: Long,
    val recipeId: Long,
    val content: String,
    val picture: String = "empty"
)
