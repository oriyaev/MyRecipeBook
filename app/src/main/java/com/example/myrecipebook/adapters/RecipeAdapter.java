package com.example.myrecipebook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrecipebook.R;
import com.example.myrecipebook.models.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private OnRecipeClickListener listener;
    private OnFavoriteClickListener favoriteListener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Recipe recipe, int position);
    }

    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, position);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    public void updateRecipe(int position, Recipe recipe) {
        recipes.set(position, recipe);
        notifyItemChanged(position);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, category;
        ImageButton favoriteButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.textViewRecipeName);
            category = itemView.findViewById(R.id.textViewCategory);
            favoriteButton = itemView.findViewById(R.id.buttonFavorite);
        }

        public void bind(Recipe recipe, int position) {
            recipeName.setText(recipe.name);
            category.setText(recipe.category);

            // Update favorite button appearance
            favoriteButton.setImageResource(recipe.isFavorite ?
                    R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });

            favoriteButton.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(recipe, position);
                }
            });
        }
    }
}