package com.example.myrecipebook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    TextInputEditText editTextRecipeName, editTextIngredients, editTextInstructions;
    MaterialButton buttonSaveRecipe;
    ImageView imageViewRecipe;
    Uri imageUri;
    AppDatabase db;
    Executor executor = Executors.newSingleThreadExecutor();

    String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Vegetarian", "Vegan"};
    boolean[] checkedCategories;
    List<String> selectedCategories = new ArrayList<>();

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        editTextInstructions = findViewById(R.id.editTextInstructions);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);
        imageViewRecipe = findViewById(R.id.imageViewRecipe);

        checkedCategories = new boolean[categories.length];

        // פותח את ה-Dialog לבחירת קטגוריות
        findViewById(R.id.autoCompleteCategory).setOnClickListener(v -> showCategoryDialog());

        // העלאת תמונה דרך אייקון בשדה הוראות
        findViewById(R.id.buttonUploadImage).setOnClickListener(view -> openImagePicker());

        db = AppDatabase.getDatabase(this);
        BottomNavigationHelper.setupBottomNavigation(this);

        buttonSaveRecipe.setOnClickListener(view -> saveRecipe());
        toolbar.setNavigationOnClickListener(v -> finish());
    }

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
                .setPositiveButton("OK", (dialog, which) -> {
                    String selectedText = String.join(", ", selectedCategories);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveRecipe() {
        String name = editTextRecipeName.getText().toString().trim();
        String category = String.join(", ", selectedCategories);
        String ingredients = editTextIngredients.getText().toString().trim();
        String instructions = editTextInstructions.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please enter recipe name and select at least one category", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasImage = imageUri != null;
        boolean hasTextContent = !ingredients.isEmpty() && !instructions.isEmpty();

        if (!hasImage && !hasTextContent) {
            Toast.makeText(this, "Add either an image or ingredients and instructions", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe(name, category, ingredients, instructions, 1);
        if (hasImage) {
            recipe.imageUri = imageUri.toString();
        }

        executor.execute(() -> {
            long recipeId = db.recipeDao().insertRecipe(recipe);
            runOnUiThread(() -> {
                if (recipeId > 0) {
                    Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error saving recipe", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
