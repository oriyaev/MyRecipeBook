package com.example.myrecipebook.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId; // Foreign key
    public String name;
    public String ingredients;
    public String instructions;
    public String category;
    public String imagePath;
    public boolean isFavorite;

    public Recipe(int userId, String name, String ingredients, String instructions, String category, String imagePath, boolean isFavorite) {
        this.userId = userId;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
        this.imagePath = imagePath;
        this.isFavorite = isFavorite;
    }
}