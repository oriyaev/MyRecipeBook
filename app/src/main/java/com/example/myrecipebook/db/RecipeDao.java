package com.example.myrecipebook.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import androidx.sqlite.db.SupportSQLiteQuery;

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

    @Query("SELECT * FROM recipes WHERE category LIKE '%' || :category || '%' AND name LIKE '%' || :searchQuery || '%'")
    List<Recipe> getRecipesByCategoryAndSearch(String searchQuery, String category);

    @Query("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();
    @RawQuery
    List<Recipe> getRecipesByIngredientsRaw(SupportSQLiteQuery query);

    @Query("SELECT * FROM recipes WHERE id IN (" +
            "SELECT recipeId FROM ingredients WHERE " +
            "(:namesCount = 0 OR name IN (:ingredientNames))" +
            ") AND (:categoryCount = 0 OR " +
            "   (" +
            "     :categoryCount > 0 AND " +
            "     (" +
            "       " +
            "       :categoryCount = 1 AND category LIKE '%' || :singleCategory || '%'" +
            "     )" +
            "   )" +
            ") AND name LIKE '%' || :searchQuery || '%' ORDER BY createdAt DESC")
    List<Recipe> getFilteredRecipes(List<String> ingredientNames, int namesCount, String singleCategory, int categoryCount, String searchQuery);

}