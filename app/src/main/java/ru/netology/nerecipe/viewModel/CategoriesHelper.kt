package ru.netology.nerecipe.viewModel

interface CategoriesHelper {
    fun getNumberOfSelectedCategories(): Int
    fun setCategoryVisible(id: Long)
    fun setCategoryInvisible(id: Long)
}