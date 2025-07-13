package com.example.myrecipebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myrecipebook.R;
import com.example.myrecipebook.adapters.RecipeAdapter;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    ListView listViewRecipes;
    FloatingActionButton fabAddRecipe;
    List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        listViewRecipes = findViewById(R.id.listViewRecipes);
        fabAddRecipe = findViewById(R.id.fabAddRecipe);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "myrecipebook.db")
                .allowMainThreadQueries()
                .build();

        recipeList = db.recipeDao().getRecipesByUser(1); // 1 זה userId – תחליפי למשתמש המחובר

        if (recipeList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No recipes yet. Tap + to add your first one!");
            emptyView.setTextSize(18);
            emptyView.setPadding(50, 200, 50, 0);
            setContentView(emptyView);
        } else {
            RecipeAdapter adapter = new RecipeAdapter(this, recipeList);
            listViewRecipes.setAdapter(adapter);

            listViewRecipes.setOnItemClickListener((parent, view, position, id) -> {
                Recipe selectedRecipe = recipeList.get(position);
                Intent intent = new Intent(this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", selectedRecipe.id);
                startActivity(intent);
            });
        }

        fabAddRecipe.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddRecipeActivity.class);
            startActivity(intent);
        });
    }
}