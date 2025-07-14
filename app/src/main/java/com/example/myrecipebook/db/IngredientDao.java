package com.example.myrecipebook.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myrecipebook.models.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {
    @Insert
    void insertIngredients(List<Ingredient> ingredients);

    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId")
    List<Ingredient> getIngredientsForRecipe(int recipeId);

    @Delete
    void deleteIngredient(Ingredient ingredient);

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    void deleteIngredientsForRecipe(int recipeId);

    @Query("SELECT DISTINCT name FROM ingredients")
    List<String> getAllIngredientNames();

    @Query("SELECT DISTINCT name FROM ingredients ORDER BY name ASC")
    List<String> getAllUniqueIngredients();

}