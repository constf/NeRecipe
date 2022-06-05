package ru.netology.nerecipe.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.dao.CategoryDao
import ru.netology.nerecipe.dao.toEntity
import ru.netology.nerecipe.dao.toModel
import ru.netology.nerecipe.data.CategoryRepository
import ru.netology.nerecipe.dto.RecCategory

class CategoryRepositoryImplementation(private val dao: CategoryDao): CategoryRepository {
    private var categories: List<RecCategory> = emptyList()
        get() = checkNotNull(data.value){"Categories data value should not be null!"}

    override lateinit var data: LiveData<List<RecCategory>>

    init {
        data = dao.getAllSortedAsc().asLiveData().map { catList ->
            catList.map { it.toModel() }
        }
    }


    override fun save(cat: RecCategory): Long {
        return dao.insert(cat.toEntity())
    }

    override fun remove(id: Long) {
        dao.removeById(id)
    }

    override fun setVisible(id: Long) {
        dao.setVisible(id)
    }

    override fun setNotVisible(id: Long) {
        dao.setNotVisible(id)
    }

    override fun getName(getId: Long): String? = dao.getNameById(getId)

}