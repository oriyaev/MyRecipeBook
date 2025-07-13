package com.example.myrecipebook.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myrecipebook.R;
import com.example.myrecipebook.activities.AddRecipeActivity;
import com.example.myrecipebook.activities.FavoritesActivity;
import com.example.myrecipebook.activities.RecipeListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomNavigationHelper {
    
    public static void setupBottomNavigation(Activity activity) {
        LinearLayout tabRecipes = activity.findViewById(R.id.tabRecipes);
        LinearLayout tabFavorites = activity.findViewById(R.id.tabFavorites);
        FloatingActionButton fabAddRecipe = activity.findViewById(R.id.fabAddRecipe);
        
        if (tabRecipes != null) {
            tabRecipes.setOnClickListener(v -> {
                if (!(activity instanceof RecipeListActivity)) {
                    Intent intent = new Intent(activity, RecipeListActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
        
        if (tabFavorites != null) {
            tabFavorites.setOnClickListener(v -> {
                if (!(activity instanceof FavoritesActivity)) {
                    Intent intent = new Intent(activity, FavoritesActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
        
        if (fabAddRecipe != null) {
            fabAddRecipe.setOnClickListener(v -> {
                Intent intent = new Intent(activity, AddRecipeActivity.class);
                activity.startActivity(intent);
            });
        }
    }
    
    public static void setActiveTab(Activity activity, String activeTab) {
        ImageView iconRecipes = activity.findViewById(R.id.iconRecipes);
        TextView labelRecipes = activity.findViewById(R.id.labelRecipes);
        ImageView iconFavorites = activity.findViewById(R.id.iconFavorites);
        TextView labelFavorites = activity.findViewById(R.id.labelFavorites);
        
        // Reset all tabs
        if (iconRecipes != null && labelRecipes != null) {
            iconRecipes.setColorFilter(ContextCompat.getColor(activity, R.color.tab_unselected));
            labelRecipes.setTextColor(ContextCompat.getColor(activity, R.color.tab_unselected));
        }
        
        if (iconFavorites != null && labelFavorites != null) {
            iconFavorites.setColorFilter(ContextCompat.getColor(activity, R.color.tab_unselected));
            labelFavorites.setTextColor(ContextCompat.getColor(activity, R.color.tab_unselected));
        }
        
        // Set active tab
        switch (activeTab) {
            case "recipes":
                if (iconRecipes != null && labelRecipes != null) {
                    iconRecipes.setColorFilter(ContextCompat.getColor(activity, R.color.tab_selected));
                    labelRecipes.setTextColor(ContextCompat.getColor(activity, R.color.tab_selected));
                }
                break;
            case "favorites":
                if (iconFavorites != null && labelFavorites != null) {
                    iconFavorites.setColorFilter(ContextCompat.getColor(activity, R.color.tab_selected));
                    labelFavorites.setTextColor(ContextCompat.getColor(activity, R.color.tab_selected));
                }
                break;
        }
    }
}