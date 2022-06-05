package ru.netology.nerecipe.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.netology.nerecipe.dto.Recipe

@Entity(tableName = "recipes",
    foreignKeys = [ForeignKey(entity = RecCategoryEntity::class,
        parentColumns = ["id_cat"], childColumns = ["category"],
        onDelete = ForeignKey.SET_DEFAULT, onUpdate = ForeignKey.CASCADE)]
)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_rec")
    val id: Long,

    @ColumnInfo(name = "name_rec")
    val name: String,

    @ColumnInfo(name = "author_rec")
    val author: String,

    @ColumnInfo(name = "category")
    val category: Long = 0L,

    @ColumnInfo(name = "is_favourite_rec")
    val isFavourite: Boolean
)

internal fun RecipeEntity.toModel(): Recipe {
    return Recipe(id, name, author, category, isFavourite)
}

internal fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(id, name, author, category, isFavourite)
}
