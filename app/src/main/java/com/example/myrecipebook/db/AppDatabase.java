package com.example.myrecipebook.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.models.User;
import com.example.myrecipebook.models.Ingredient;

@Database(entities = {Recipe.class, User.class, Ingredient.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract UserDao userDao();
    public abstract IngredientDao ingredientDao();

    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    //context.getApplicationContext().deleteDatabase("myrecipebook.db");

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "myrecipebook.db")
                            .fallbackToDestructiveMigration()
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}