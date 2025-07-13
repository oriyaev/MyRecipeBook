package com.example.myrecipebook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
import java.util.List;

public class RecipeListActivity extends AppCompatActivity implements
        RecipeAdapter.OnRecipeClickListener, RecipeAdapter.OnFavoriteClickListener {

    RecyclerView recyclerViewRecipes;
    EditText searchBar;
    Spinner categoryFilter;
    List<Recipe> favoriteRecipes;
    LinearLayout emptyStateView;
    List<Recipe> recipeList;
    RecipeAdapter adapter;
    AppDatabase db;

    String[] categories = {"All", "Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Vegetarian", "Vegan"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        emptyStateView = findViewById(R.id.emptyStateView);  // Initialize the empty state view
        searchBar = findViewById(R.id.searchBar);
        categoryFilter = findViewById(R.id.categoryFilter);

        db = AppDatabase.getDatabase(this);

        // Setup RecyclerView
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipeList);
        adapter.setOnRecipeClickListener(this);
        adapter.setOnFavoriteClickListener(this);
        recyclerViewRecipes.setAdapter(adapter);

        // Setup bottom navigation
        BottomNavigationHelper.setupBottomNavigation(this);
        BottomNavigationHelper.setActiveTab(this, "recipes");

        // Setup category filter
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilter.setAdapter(categoryAdapter);

        categoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadRecipes(searchBar.getText().toString(), categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadRecipes(searchBar.getText().toString(), "All");
            }
        });

        // Search bar listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                loadRecipes(charSequence.toString(), categories[categoryFilter.getSelectedItemPosition()]);
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {
            }
        });

        loadRecipes("", "All");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(searchBar.getText().toString(), categories[categoryFilter.getSelectedItemPosition()]);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadRecipes(final String query, final String category) {
        new AsyncTask<Void, Void, List<Recipe>>() {
            @Override
            protected List<Recipe> doInBackground(Void... voids) {
                // Modify the query to filter by both category and search query
                if (category.equals("All")) {
                    return db.recipeDao().getRecipesBySearch(query);
                } else {
                    return db.recipeDao().getRecipesByCategoryAndSearch(query, category);
                }
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

    @SuppressLint("StaticFieldLeak")
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