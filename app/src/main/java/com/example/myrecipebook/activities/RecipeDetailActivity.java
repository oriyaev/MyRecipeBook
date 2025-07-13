package com.example.myrecipebook.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;

public class RecipeDetailActivity extends AppCompatActivity {

    TextView textViewRecipeName, textViewCategory, textViewIngredients, textViewInstructions;
    ImageButton buttonFavorite;
    AppDatabase db;
    Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        textViewRecipeName = findViewById(R.id.textViewRecipeName);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewIngredients = findViewById(R.id.textViewIngredients);
        textViewInstructions = findViewById(R.id.textViewInstructions);
        buttonFavorite = findViewById(R.id.buttonFavorite);

        db = AppDatabase.getDatabase(this);

        // Setup bottom navigation
        BottomNavigationHelper.setupBottomNavigation(this);

        int recipeId = getIntent().getIntExtra("recipeId", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe(recipeId);

        // Handle back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Handle favorite button
        buttonFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void loadRecipe(int recipeId) {
        new AsyncTask<Void, Void, Recipe>() {
            @Override
            protected Recipe doInBackground(Void... voids) {
                return db.recipeDao().getRecipeById(recipeId);
            }

            @Override
            protected void onPostExecute(Recipe recipe) {
                if (recipe != null) {
                    currentRecipe = recipe;
                    displayRecipe(recipe);
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute();
    }

    private void displayRecipe(Recipe recipe) {
        textViewRecipeName.setText(recipe.name);
        textViewCategory.setText(recipe.category);
        textViewIngredients.setText(recipe.ingredients);
        textViewInstructions.setText(recipe.instructions);

        updateFavoriteButton(recipe.isFavorite);
    }

    private void updateFavoriteButton(boolean isFavorite) {
        buttonFavorite.setImageResource(isFavorite ?
                R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private void toggleFavorite() {
        if (currentRecipe == null) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                currentRecipe.isFavorite = !currentRecipe.isFavorite;
                db.recipeDao().updateFavoriteStatus(currentRecipe.id, currentRecipe.isFavorite);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButton(currentRecipe.isFavorite);
                String message = currentRecipe.isFavorite ?
                        "Added to favorites" : "Removed from favorites";
                Toast.makeText(RecipeDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}