package com.example.myrecipebook.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myrecipebook.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipes WHERE userId = :userId ORDER BY createdAt DESC")
    List<Recipe> getRecipesByUser(int userId);

    @Query("SELECT * FROM recipes WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    List<Recipe> getFavoriteRecipesByUser(int userId);

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe getRecipeById(int id);

    @Insert
    long insertRecipe(Recipe recipe);

    @Update
    void updateRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    void updateFavoriteStatus(int recipeId, boolean isFavorite);

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :searchQuery || '%'")
    List<Recipe> getRecipesBySearch(String searchQuery);

    @Query("SELECT * FROM recipes WHERE category IN (:category) AND name LIKE '%' || :searchQuery || '%'")
    List<Recipe> getRecipesByCategoryAndSearch(String searchQuery, String category);
}