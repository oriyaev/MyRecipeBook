package com.example.myrecipebook.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.models.User;

@Database(entities = {User.class, Recipe.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract RecipeDao recipeDao();
}