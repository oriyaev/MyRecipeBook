package com.example.myrecipebook.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.example.myrecipebook.models.Ingredient;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    TextView textViewRecipeName, textViewCategory, textViewIngredients, textViewInstructions;
    ImageButton buttonFavorite;
    MaterialButton buttonDeleteRecipe;
    AppDatabase db;
    Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textViewRecipeName = findViewById(R.id.textViewRecipeName);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewIngredients = findViewById(R.id.textViewIngredients);
        textViewInstructions = findViewById(R.id.textViewInstructions);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonDeleteRecipe = findViewById(R.id.buttonDeleteRecipe);

        buttonDeleteRecipe.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_color));
        buttonDeleteRecipe.setOnClickListener(v -> deleteRecipe());

        db = AppDatabase.getDatabase(this);
        BottomNavigationHelper.setupBottomNavigation(this);

        int recipeId = getIntent().getIntExtra("recipeId", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe(recipeId);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        buttonFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void loadRecipe(int recipeId) {
        new AsyncTask<Void, Void, Recipe>() {
            List<Ingredient> ingredientList;

            @Override
            protected Recipe doInBackground(Void... voids) {
                Recipe recipe = db.recipeDao().getRecipeById(recipeId);
                if (recipe != null) {
                    // Load ingredients with their measurements from the database
                    ingredientList = db.ingredientDao().getIngredientsForRecipe(recipeId);
                }
                return recipe;
            }

            @Override
            protected void onPostExecute(Recipe recipe) {
                if (recipe != null) {
                    currentRecipe = recipe;
                    displayRecipe(recipe, ingredientList);
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute();
    }

    private void displayRecipe(Recipe recipe, List<Ingredient> ingredientList) {
        textViewRecipeName.setText(recipe.name);
        textViewCategory.setText(recipe.category);

        // Build the ingredients string with name and measurement
        StringBuilder ingredientsText = new StringBuilder();
        for (Ingredient ingredient : ingredientList) {
            ingredientsText.append("• ")
                    .append(ingredient.name)
                    .append(" - ")
                    .append(ingredient.measure) // Add the measurement after the ingredient name
                    .append("\n");
        }
        textViewIngredients.setText(ingredientsText.toString());

        textViewInstructions.setText(recipe.instructions);
        updateFavoriteButton(recipe.isFavorite);

        // Load recipe image using Glide
        if (recipe.imageUri != null && !recipe.imageUri.isEmpty()) {
            ImageView imageViewRecipe = findViewById(R.id.imageViewRecipe);
            imageViewRecipe.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Uri.parse(recipe.imageUri))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .into(imageViewRecipe);
        }
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

    private void deleteRecipe() {
        if (currentRecipe == null) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.recipeDao().deleteRecipe(currentRecipe);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(RecipeDetailActivity.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the recipe list
            }
        }.execute();
    }
}
