package com.example.myrecipebook.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myrecipebook.R;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {

    TextView textViewRecipeName, textViewIngredients, textViewInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        textViewRecipeName = findViewById(R.id.textViewRecipeName);
        textViewIngredients = findViewById(R.id.textViewIngredients);
        textViewInstructions = findViewById(R.id.textViewInstructions);

        int recipeId = getIntent().getIntExtra("recipeId", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "myrecipebook.db")
                .allowMainThreadQueries()
                .build();

        Recipe recipe = db.recipeDao().getRecipeById(recipeId);

        if (recipe != null) {
            textViewRecipeName.setText(recipe.name);
            textViewIngredients.setText(recipe.ingredients);
            textViewInstructions.setText(recipe.instructions);
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}