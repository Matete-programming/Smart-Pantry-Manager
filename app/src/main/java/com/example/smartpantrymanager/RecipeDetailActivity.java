
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView txtName = findViewById(R.id.txtDetailRecipeName);
        TextView txtIngredients = findViewById(R.id.txtDetailIngredients);
        TextView txtInstructions = findViewById(R.id.txtDetailInstructions);
        Button btnBack = findViewById(R.id.btnBackFromRecipe);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        btnBack.setOnClickListener(v -> finish());

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe(recipeId, txtName, txtIngredients, txtInstructions);
    }

    private void loadRecipe(
            int recipeId,
            TextView txtName,
            TextView txtIngredients,
            TextView txtInstructions) {

        try (Cursor recipe = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT name, instructions FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)})) {

            if (!recipe.moveToFirst()) {
                Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            txtName.setText(recipe.getString(0));
            txtInstructions.setText(recipe.getString(1));
        }

        StringBuilder ingredientList = new StringBuilder();

        try (Cursor ingredients = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)})) {

            while (ingredients.moveToNext()) {
                String name = ingredients.getString(0);
                double quantity = ingredients.getDouble(1);
                String unit = ingredients.getString(2);

                ingredientList.append("• ")
                        .append(name)
                        .append(" — ")
                        .append(quantity)
                        .append(" ")
                        .append(unit)
                        .append("\n");
            }
        }

        txtIngredients.setText(ingredientList.toString());
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
