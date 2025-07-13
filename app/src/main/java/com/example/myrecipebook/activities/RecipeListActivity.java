package com.example.myrecipebook.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrecipebook.R;
import com.example.myrecipebook.adapters.RecipeAdapter;
import com.example.myrecipebook.db.AppDatabase;
import com.example.myrecipebook.models.Recipe;
import com.example.myrecipebook.utils.BottomNavigationHelper;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity implements
        RecipeAdapter.OnRecipeClickListener, RecipeAdapter.OnFavoriteClickListener {

    RecyclerView recyclerViewRecipes;
    LinearLayout emptyStateView;
    List<Recipe> recipeList;
    RecipeAdapter adapter;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        emptyStateView = findViewById(R.id.emptyStateView);

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

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        new AsyncTask<Void, Void, List<Recipe>>() {
            @Override
            protected List<Recipe> doInBackground(Void... voids) {
                return db.recipeDao().getRecipesByUser(1);
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