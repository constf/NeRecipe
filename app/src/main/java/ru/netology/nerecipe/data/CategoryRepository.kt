package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.RecCategory

interface CategoryRepository {
    val data: LiveData<List<RecCategory>>

    fun save(cat: RecCategory): Long
    fun remove(id: Long)

    fun setVisible(id: Long)
    fun setNotVisible(id: Long)

    fun getName(getId: Long): String?
}