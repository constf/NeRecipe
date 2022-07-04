package ru.netology.nerecipe.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nerecipe.dto.RecCategory

@Entity(tableName = "categories")
data class RecCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cat")
    val id: Long,

    @ColumnInfo(name = "name_cat")
    val name: String,

    @ColumnInfo(name = "show_or_not")
    val showOrNot: Boolean
)


internal fun RecCategory.toEntity(): RecCategoryEntity {
    return RecCategoryEntity(id, name, showOrNot)
}

internal fun RecCategoryEntity.toModel(): RecCategory {
    return RecCategory(id, name, showOrNot)
}