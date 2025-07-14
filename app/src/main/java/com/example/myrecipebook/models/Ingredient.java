package com.example.myrecipebook.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int recipeId; // קישור ל-Recipe

    public String name; // שם המצרך
}