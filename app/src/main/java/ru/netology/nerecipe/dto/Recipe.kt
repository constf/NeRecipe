package ru.netology.nerecipe.dto

data class Recipe(
    val id: Long,
    val name: String,
    val author: String,
    val category: Long,
    val isFavourite: Boolean
)
