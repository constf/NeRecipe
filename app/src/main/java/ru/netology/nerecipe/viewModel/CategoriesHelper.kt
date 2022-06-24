package ru.netology.nerecipe.viewModel

interface CategoriesHelper {
    fun getNumberOfSelectedCategories(): Int
    fun setCetegoryVisible(id: Long)
    fun setCetegoryInvisible(id: Long)
}