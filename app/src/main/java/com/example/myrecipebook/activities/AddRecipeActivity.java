package com.example.myrecipebook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Ingredient;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    EditText editTextRecipeName, editTextInstructions;
    LinearLayout ingredientsContainer;
    MaterialButton buttonSaveRecipe;
    ImageView imageViewRecipe;
    Uri imageUri;
    AppDatabase db;
    Executor executor = Executors.newSingleThreadExecutor();

    String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Vegetarian", "Vegan"};
    boolean[] checkedCategories;
    List<String> selectedCategories = new ArrayList<>();

    List<IngredientRow> ingredientRows = new ArrayList<>();

    // Declare AutoCompleteTextViews for Difficulty
    Spinner spinnerDifficulty;
    ArrayAdapter<String> difficultyAdapter;
    TextInputEditText editTextCookingTime;

    // Initialize launcher for image picker
    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageViewRecipe.setImageURI(imageUri);
                    imageViewRecipe.setVisibility(ImageView.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Set up toolbar and back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind UI elements
        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextInstructions = findViewById(R.id.editTextInstructions);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);
        imageViewRecipe = findViewById(R.id.imageViewRecipe);
        editTextCookingTime = findViewById(R.id.editTextCookingTime);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);

        // Create and set the Adapter for the Spinner
        String[] difficultyLevels = {"Easy", "Medium", "Hard"};
        difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficultyLevels);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        // Set category selection on click
        findViewById(R.id.autoCompleteCategory).setOnClickListener(v -> showCategoryDialog());
        // Set image upload button listener
        findViewById(R.id.buttonUploadImage).setOnClickListener(view -> openImagePicker());

        // Initialize database
        db = AppDatabase.getDatabase(this);
        BottomNavigationHelper.setupBottomNavigation(this);

        addNewIngredientField(); // Start with one blank row for ingredients

        buttonSaveRecipe.setOnClickListener(view -> saveRecipe());
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Method to display category selection dialog
    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Categories")
                .setMultiChoiceItems(categories, checkedCategories, (dialog, indexSelected, isChecked) -> {
                    if (isChecked) {
                        if (!selectedCategories.contains(categories[indexSelected])) {
                            selectedCategories.add(categories[indexSelected]);
                        }
                    } else {
                        selectedCategories.remove(categories[indexSelected]);
                    }
                })
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Open image picker for recipe image
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Add a new ingredient input field with measurement
    private void addNewIngredientField() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        AutoCompleteTextView nameInput = new AutoCompleteTextView(this);
        nameInput.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        nameInput.setHint("Ingredient");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

        EditText quantityInput = new EditText(this);
        quantityInput.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        quantityInput.setHint("e.g. 2 cups");
        quantityInput.setInputType(InputType.TYPE_CLASS_TEXT);

        MaterialButton removeButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        removeButton.setText("✖");
        removeButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        removeButton.setOnClickListener(v -> {
            ingredientsContainer.removeView(row);
            for (int i = 0; i < ingredientRows.size(); i++) {
                if (ingredientRows.get(i).row == row) {
                    ingredientRows.remove(i);
                    break;
                }
            }
        });

        row.addView(nameInput);
        row.addView(quantityInput);
        row.addView(removeButton);

        ingredientsContainer.addView(row);

        IngredientRow ingredientRow = new IngredientRow(row, nameInput, quantityInput);
        ingredientRows.add(ingredientRow);

        nameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && ingredientRows.get(ingredientRows.size() - 1).row == row) {
                addNewIngredientField();
            }
        });
    }

    // Save the recipe data to the database
    private void saveRecipe() {
        String name = editTextRecipeName.getText().toString().trim();
        String instructions = editTextInstructions.getText().toString().trim();
        String category = String.join(", ", selectedCategories);
        String cookingTime = editTextCookingTime.getText().toString().trim();
        String difficulty = spinnerDifficulty.getSelectedItem().toString().trim();

        if (name.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please enter recipe name and category", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe(name, category, "", instructions, 1);
        recipe.cookingTime = cookingTime;
        recipe.difficulty = difficulty;

        if (imageUri != null) {
            recipe.imageUri = imageUri.toString();
        }

        executor.execute(() -> {
            long recipeId = db.recipeDao().insertRecipe(recipe);

            if (recipeId > 0) {
                List<Ingredient> ingredientsToSave = new ArrayList<>();
                for (IngredientRow r : ingredientRows) {
                    String ingredientName = r.nameInput.getText().toString().trim().toLowerCase();
                    String measure = r.measureInput.getText().toString().trim();

                    // Save ingredient only if both name and measurement are valid
                    if (!ingredientName.isEmpty() && !measure.isEmpty()) {
                        Ingredient ing = new Ingredient();
                        ing.recipeId = (int) recipeId;
                        ing.name = ingredientName;
                        ing.measure = measure; // Store measurement separately
                        ingredientsToSave.add(ing);
                    }
                }

                db.ingredientDao().insertIngredients(ingredientsToSave);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Error saving recipe", Toast.LENGTH_SHORT).show());
            }
        });
    }

    static class IngredientRow {
        LinearLayout row;
        AutoCompleteTextView nameInput;
        EditText measureInput;

        IngredientRow(LinearLayout row, AutoCompleteTextView nameInput, EditText measureInput) {
            this.row = row;
            this.nameInput = nameInput;
            this.measureInput = measureInput;
        }
    }
}
