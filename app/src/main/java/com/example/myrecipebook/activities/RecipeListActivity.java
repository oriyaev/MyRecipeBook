package com.example.myrecipebook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrecipebook.R;
import com.example.myrecipebook.adapters.RecipeAdapter;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity implements
        RecipeAdapter.OnRecipeClickListener, RecipeAdapter.OnFavoriteClickListener {

    RecyclerView recyclerViewRecipes;
    EditText searchBar;
    AutoCompleteTextView autoCompleteCategory, autoCompleteIngredients;
    LinearLayout emptyStateView;
    List<Recipe> recipeList = new ArrayList<>();
    RecipeAdapter adapter;
    AppDatabase db;

    String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Vegetarian", "Vegan"};
    boolean[] checkedCategories = new boolean[categories.length];
    List<String> selectedCategories = new ArrayList<>();

    List<String> allIngredients = new ArrayList<>();
    boolean[] checkedIngredients;
    List<String> selectedIngredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        emptyStateView = findViewById(R.id.emptyStateView);
        searchBar = findViewById(R.id.searchBar);
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);
        autoCompleteIngredients = findViewById(R.id.autoCompleteIngredients);

        db = AppDatabase.getDatabase(this);

        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipeList);
        adapter.setOnRecipeClickListener(this);
        adapter.setOnFavoriteClickListener(this);
        recyclerViewRecipes.setAdapter(adapter);

        BottomNavigationHelper.setupBottomNavigation(this);
        BottomNavigationHelper.setActiveTab(this, "recipes");

        autoCompleteCategory.setOnClickListener(v -> showCategoryDialog());
        autoCompleteIngredients.setOnClickListener(v -> showIngredientDialog());

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadRecipes(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        loadIngredientsForFilter();
        loadRecipes("");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadIngredientsForFilter();
        loadRecipes(searchBar.getText().toString());
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Categories")
                .setMultiChoiceItems(categories, checkedCategories, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!selectedCategories.contains(categories[which])) {
                            selectedCategories.add(categories[which]);
                        }
                    } else {
                        selectedCategories.remove(categories[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    autoCompleteCategory.setText(String.join(", ", selectedCategories));
                    loadRecipes(searchBar.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showIngredientDialog() {
        if (allIngredients == null || allIngredients.isEmpty()) return;
        String[] items = allIngredients.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Ingredients")
                .setMultiChoiceItems(items, checkedIngredients, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!selectedIngredients.contains(items[which])) {
                            selectedIngredients.add(items[which]);
                        }
                    } else {
                        selectedIngredients.remove(items[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    autoCompleteIngredients.setText(String.join(", ", selectedIngredients));
                    loadRecipes(searchBar.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadIngredientsForFilter() {
        new AsyncTask<Void, Void, List<String>>() {
            boolean usedFallback = false;

            @Override
            protected List<String> doInBackground(Void... voids) {
                try {
                    List<String> ingredientNames = db.ingredientDao().getAllIngredientNames();
                    if (ingredientNames == null || ingredientNames.isEmpty()) {
                        throw new Exception("Empty ingredient list");
                    }
                    return ingredientNames;
                } catch (Exception e) {
                    usedFallback = true;
                    return Arrays.asList("Eggs", "Milk", "Flour", "Sugar", "Butter", "Salt", "Pepper",
                            "Cheese", "Tomatoes", "Onions", "Garlic", "Bread", "Chicken", "Rice");
                }
            }

            @Override
            protected void onPostExecute(List<String> list) {
                allIngredients = list;
                checkedIngredients = new boolean[list.size()];
                autoCompleteIngredients.setText(""); // reset
                if (usedFallback) {
                    Toast.makeText(RecipeListActivity.this, "Fallback ingredients used", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadRecipes(final String query) {
        new AsyncTask<Void, Void, List<Recipe>>() {
            @Override
            protected List<Recipe> doInBackground(Void... voids) {
                return db.recipeDao().getFilteredRecipes(
                        selectedIngredients,
                        selectedIngredients.size(),
                        selectedCategories.isEmpty() ? "" : selectedCategories.get(0),
                        selectedCategories.size(),
                        query
                );
            }

            @Override
            protected void onPostExecute(List<Recipe> recipes) {
                recipeList = recipes;
                if (recipes.isEmpty()) {
                    recyclerViewRecipes.setVisibility(View.GONE);
                    emptyStateView.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewRecipes.setVisibility(View.VISIBLE);
                    emptyStateView.setVisibility(View.GONE);
                    adapter.updateRecipes(recipes);
                }
            }
        }.execute();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.id);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Recipe recipe, int position) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                recipe.isFavorite = !recipe.isFavorite;
                db.recipeDao().updateFavoriteStatus(recipe.id, recipe.isFavorite);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.updateRecipe(position, recipe);
            }
        }.execute();
    }
}