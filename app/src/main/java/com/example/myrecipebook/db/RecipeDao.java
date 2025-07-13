package com.example.myrecipebook.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.myrecipebook.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    void insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipes WHERE userId = :userId")
    List<Recipe> getRecipesByUser(int userId);

    @Query("SELECT * FROM recipes WHERE userId = :userId AND isFavorite = 1")
    List<Recipe> getFavoriteRecipes(int userId);

    @Query("SELECT * FROM recipes WHERE userId = :userId AND category = :category")
    List<Recipe> getRecipesByCategory(int userId, String category);

    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    Recipe getRecipeById(int recipeId);
}