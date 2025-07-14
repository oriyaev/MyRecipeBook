package com.example.myrecipebook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    TextInputEditText editTextRecipeName, editTextInstructions;
    MaterialButton buttonSaveRecipe;
    ImageView imageViewRecipe;
    LinearLayout ingredientsContainer;

    Uri imageUri;
    AppDatabase db;
    Executor executor = Executors.newSingleThreadExecutor();

    String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Vegetarian", "Vegan"};
    boolean[] checkedCategories;
    List<String> selectedCategories = new ArrayList<>();
    List<TextInputEditText> ingredientInputs = new ArrayList<>();

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
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextInstructions = findViewById(R.id.editTextInstructions);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);
        imageViewRecipe = findViewById(R.id.imageViewRecipe);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);

        checkedCategories = new boolean[categories.length];

        findViewById(R.id.autoCompleteCategory).setOnClickListener(v -> showCategoryDialog());
        findViewById(R.id.buttonUploadImage).setOnClickListener(v -> openImagePicker());

        db = AppDatabase.getDatabase(this);
        BottomNavigationHelper.setupBottomNavigation(this);

        addNewIngredientField(); // First ingredient input

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
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    private void addNewIngredientField() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextInputLayout layout = new TextInputLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        layout.setHint("Ingredient");

        TextInputEditText editText = new TextInputEditText(this);
        layout.addView(editText);

        // Add remove button
        MaterialButton removeButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        removeButton.setText("✖");
        removeButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        removeButton.setOnClickListener(v -> {
            ingredientsContainer.removeView(row);
            ingredientInputs.remove(editText);
        });

        row.addView(layout);
        row.addView(removeButton);

        ingredientsContainer.addView(row);
        ingredientInputs.add(editText);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && ingredientInputs.get(ingredientInputs.size() - 1) == v) {
                addNewIngredientField(); // Add new blank one only when the last gets focus
            }
        });
    }

    private void saveRecipe() {
        String name = editTextRecipeName.getText().toString().trim();
        String category = String.join(", ", selectedCategories);
        String instructions = editTextInstructions.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please enter recipe name and select at least one category", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasImage = imageUri != null;
        boolean hasInstructions = !instructions.isEmpty();

        List<String> ingredientNames = new ArrayList<>();
        for (TextInputEditText input : ingredientInputs) {
            String ing = input.getText() != null ? input.getText().toString().trim() : "";
            if (!ing.isEmpty()) {
                ingredientNames.add(ing);
            }
        }

        if (!hasImage && (!hasInstructions || ingredientNames.isEmpty())) {
            Toast.makeText(this, "Add either an image or full instructions and ingredients", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe(name, category, "", instructions, 1);
        if (hasImage) recipe.imageUri = imageUri.toString();

        executor.execute(() -> {
            long recipeId = db.recipeDao().insertRecipe(recipe);
            if (recipeId > 0) {
                List<Ingredient> ingredientList = new ArrayList<>();
                for (String nameStr : ingredientNames) {
                    Ingredient i = new Ingredient();
                    i.recipeId = (int) recipeId;
                    i.name = nameStr;
                    ingredientList.add(i);
                }
                db.ingredientDao().insertIngredients(ingredientList);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error saving recipe", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
