package com.example.myrecipebook.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String category;
    public String ingredients;
    public String instructions;
    public int userId;
    public boolean isFavorite;
    public long createdAt;
    public String imageUri;

    public Recipe() {
        this.createdAt = System.currentTimeMillis();
        this.isFavorite = false;
    }

    public Recipe(String name, String category, String ingredients, String instructions, int userId) {
        this.name = name;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.userId = userId;
        this.createdAt = System.currentTimeMillis();
        this.isFavorite = false;
    }
}