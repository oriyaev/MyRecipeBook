package com.example.myrecipebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.example.myrecipebook.R;
import com.example.myrecipebook.models.Recipe;

import java.util.List;

public class RecipeAdapter extends BaseAdapter {

    final private Context context;
    final private List<Recipe> recipeList;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return recipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recipeList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        }

        TextView textViewRecipeName = convertView.findViewById(R.id.textViewRecipeName);
        TextView textViewCategory = convertView.findViewById(R.id.textViewCategory);
        TextView textViewFavorite = convertView.findViewById(R.id.textViewFavorite);

        Recipe recipe = recipeList.get(position);

        textViewRecipeName.setText(recipe.name);
        textViewCategory.setText(recipe.category);

        // אם מועדף, להציג את הסימון
        if (recipe.isFavorite) {
            textViewFavorite.setVisibility(View.VISIBLE);
            textViewFavorite.setText("R.string.favorite_label");
        } else {
            textViewFavorite.setVisibility(View.GONE);
        }

        return convertView;
    }
}