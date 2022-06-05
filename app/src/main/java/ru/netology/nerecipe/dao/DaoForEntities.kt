package ru.netology.nerecipe.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes " +
            "INNER JOIN categories ON categories.id_cat = recipes.category " +
            "WHERE categories.show_or_not = 1")
    fun getAllFilteredRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newRecipe: RecipeEntity) : Long

    @Query("DELETE FROM recipes WHERE id_rec = :remId")
    fun removeById(remId: Long)

    @Query("UPDATE recipes SET " +
            "is_favourite_rec = CASE WHEN :favourite THEN 1 ELSE 0 END " +
            "WHERE id_rec = :id")
    fun setFavourite(id: Long, favourite: Boolean)
}

@Dao
interface RecStepsDao {
    @Query("SELECT * FROM recipe_steps ORDER BY id_step ASC")
    fun getAllRecipeSteps(): Flow<List<RecStepEntity>>

    @Query("SELECT * FROM recipe_steps " +
            "INNER JOIN recipes ON recipes.id_rec = recipe_steps.rec_id " +
            "INNER JOIN categories ON categories.id_cat = recipes.category WHERE categories.show_or_not = 1 " +
            "ORDER BY recipe_steps.id_step ASC")
    fun getFilteredRecipeSteps(): Flow<List<RecStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newStep: RecStepEntity): Long

    @Query("DELETE FROM recipe_steps WHERE id_step = :remId")
    fun removeById(remId: Long)

    @Query("UPDATE recipe_steps SET rec_id = :newId WHERE rec_id = :oldId")
    fun updateRecipesIds(oldId: Long, newId: Long)

}

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name_cat ASC")
    fun getAllSortedAsc(): Flow<List<RecCategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name_cat DESC")
    fun getAllSortedDesc(): Flow<List<RecCategoryEntity>>

    @Query("SELECT name_cat FROM categories WHERE id_cat = :id")
    fun getNameById(id: Long): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inCategory: RecCategoryEntity): Long

    @Query("DELETE FROM categories WHERE id_cat = :remId")
    fun removeById(remId: Long)

    @Query("UPDATE categories SET show_or_not = 1 WHERE id_cat = :id")
    fun setVisible(id: Long)

    @Query("UPDATE categories SET show_or_not = 0 WHERE id_cat = :id")
    fun setNotVisible(id: Long)
}
