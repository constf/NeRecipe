package ru.netology.nerecipe.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.netology.nerecipe.dto.RecipeStep

@Entity(tableName = "recipe_steps",
    foreignKeys = [ForeignKey(entity = RecipeEntity::class,
    parentColumns = ["id_rec"], childColumns = ["rec_id"],
    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.NO_ACTION)]
)
data class RecStepEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_step")
    val id: Long,

    @ColumnInfo(name = "rec_id")
    val recipeId: Long,

    @ColumnInfo(name = "step_content")
    val content: String,

    @ColumnInfo(name = "step_picture")
    val picture: String
)

internal fun RecStepEntity.toModel(): RecipeStep {
    return RecipeStep(id, recipeId, content, picture)
}

internal fun RecipeStep.toEntity(): RecStepEntity {
    return RecStepEntity(id, recipeId, content, picture)
}
