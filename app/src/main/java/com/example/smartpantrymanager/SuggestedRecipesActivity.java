
package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtMessage;
    private RecyclerView recyclerRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        txtMessage = findViewById(R.id.txtRecipeMessage);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        Button btnBack = findViewById(R.id.btnBackToPantry);

        databaseHelper = new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        loadAvailableRecipes();
    }

    private void loadAvailableRecipes() {
        List<Recipe> recipes =
                RecipeMatcher.getAvailableRecipes(databaseHelper);

        recyclerRecipes.setAdapter(new RecipeAdapter(
                recipes,
                recipe -> {
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class);

                    intent.putExtra("recipe_id", recipe.getId());
                    startActivity(intent);
                }));

        if (recipes.isEmpty()) {
            txtMessage.setText(
                    "No complete recipe matches your pantry yet. " +
                            "Add ingredients or increase their quantities.");
        } else {
            txtMessage.setText(
                    recipes.size() + " recipe(s) you can make " +
                            "with your current ingredients.");
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recyclerRecipes != null) {
            loadAvailableRecipes();
        }
    }
}
