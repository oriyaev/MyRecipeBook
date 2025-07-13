package com.example.myrecipebook.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddRecipeActivity extends AppCompatActivity {

    TextInputEditText editTextRecipeName, editTextCategory, editTextIngredients, editTextInstructions;
    MaterialButton buttonSaveRecipe;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        editTextInstructions = findViewById(R.id.editTextInstructions);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);

        db = AppDatabase.getDatabase(this);

        // Setup bottom navigation
        BottomNavigationHelper.setupBottomNavigation(this);

        buttonSaveRecipe.setOnClickListener(view -> saveRecipe());

        // Handle back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void saveRecipe() {
        String name = editTextRecipeName.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String ingredients = editTextIngredients.getText().toString().trim();
        String instructions = editTextInstructions.getText().toString().trim();

        if (name.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe(name, category, ingredients, instructions, 1);

        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                return db.recipeDao().insertRecipe(recipe);
            }

            @Override
            protected void onPostExecute(Long recipeId) {
                if (recipeId > 0) {
                    Toast.makeText(AddRecipeActivity.this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRecipeActivity.this, "Error saving recipe", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}