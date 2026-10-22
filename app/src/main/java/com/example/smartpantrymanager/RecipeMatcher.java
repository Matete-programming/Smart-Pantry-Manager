
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    private static class ConvertedQuantity {
        final String dimension;
        final double quantity;

        ConvertedQuantity(String dimension, double quantity) {
            this.dimension = dimension;
            this.quantity = quantity;
        }
    }

    public static List<Recipe> getAvailableRecipes(DatabaseHelper helper) {
        List<Recipe> availableRecipes = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        // Stock is stored in standard units:
        // grams, millilitres, pieces, or another compatible unit.
        Map<String, Double> pantryStock = new HashMap<>();

        try (Cursor pantry = db.rawQuery(
                "SELECT name, quantity, unit FROM pantry_items",
                null)) {

            while (pantry.moveToNext()) {
                String ingredientName = normalizeIngredient(
                        pantry.getString(0));

                double quantity = pantry.getDouble(1);
                String unit = pantry.getString(2);

                ConvertedQuantity converted =
                        convertQuantity(quantity, unit);

                if (ingredientName.isEmpty() || converted == null) {
                    continue;
                }

                String key = ingredientName + "|" + converted.dimension;

                pantryStock.put(
                        key,
                        pantryStock.getOrDefault(key, 0.0)
                                + converted.quantity);
            }
        }

        try (Cursor recipes = db.rawQuery(
                "SELECT id, name, instructions FROM recipes",
                null)) {

            while (recipes.moveToNext()) {
                int recipeId = recipes.getInt(0);
                String recipeName = recipes.getString(1);
                String instructions = recipes.getString(2);

                if (hasAllIngredients(db, recipeId, pantryStock)) {
                    availableRecipes.add(
                            new Recipe(recipeId, recipeName, instructions));
                }
            }
        }

        return availableRecipes;
    }

    private static boolean hasAllIngredients(
            SQLiteDatabase db,
            int recipeId,
            Map<String, Double> pantryStock) {

        try (Cursor ingredients = db.rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)})) {

            // An empty recipe must not be suggested.
            if (!ingredients.moveToFirst()) {
                return false;
            }

            do {
                String name = normalizeIngredient(
                        ingredients.getString(0));

                double requiredQuantity = ingredients.getDouble(1);
                String requiredUnit = ingredients.getString(2);

                ConvertedQuantity required =
                        convertQuantity(requiredQuantity, requiredUnit);

                if (name.isEmpty() || required == null
                        || !Double.isFinite(required.quantity)
                        || required.quantity <= 0) {
                    return false;
                }

                String key = name + "|" + required.dimension;

                double available = pantryStock.getOrDefault(key, 0.0);

                // A tiny tolerance avoids floating-point rounding errors.
                if (available + 0.000001 < required.quantity) {
                    return false;
                }

            } while (ingredients.moveToNext());
        }

        return true;
    }

    private static String normalizeIngredient(String name) {
        if (name == null) {
            return "";
        }

        String value = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        // Common irregular or ambiguous plural forms.
        switch (value) {
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "leaves":
                return "leaf";
            case "loaves":
                return "loaf";
            case "berries":
                return "berry";
        }

        // Basic plural handling.
        if (value.endsWith("ies") && value.length() > 3) {
            return value.substring(0, value.length() - 3) + "y";
        }

        if (value.endsWith("oes") && value.length() > 3) {
            return value.substring(0, value.length() - 2);
        }

        if ((value.endsWith("ches")
                || value.endsWith("shes")
                || value.endsWith("xes")
                || value.endsWith("zes"))
                && value.length() > 4) {
            return value.substring(0, value.length() - 2);
        }

        if (value.endsWith("s")
                && !value.endsWith("ss")
                && value.length() > 2) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

    private static ConvertedQuantity convertQuantity(
            double quantity, String unit) {

        if (!Double.isFinite(quantity) || quantity <= 0
                || unit == null) {
            return null;
        }

        String normalizedUnit = unit.trim()
                .toLowerCase(Locale.ROOT);

        switch (normalizedUnit) {
            case "g":
            case "gram":
            case "grams":
                return new ConvertedQuantity("weight_g", quantity);

            case "kg":
            case "kilogram":
            case "kilograms":
                return new ConvertedQuantity("weight_g", quantity * 1000);

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return new ConvertedQuantity("volume_ml", quantity);

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return new ConvertedQuantity("volume_ml", quantity * 1000);

            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return new ConvertedQuantity("volume_ml", quantity * 5);

            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return new ConvertedQuantity("volume_ml", quantity * 15);

            case "cup":
            case "cups":
                return new ConvertedQuantity("volume_ml", quantity * 240);

            case "piece":
            case "pieces":
            case "pc":

            case "pcs":
            case "unit":
            case "units":
                return new ConvertedQuantity("count", quantity);

            default:
                // Unknown units can only match the same unit.
                // For example, "pack" cannot match "g".
                return new ConvertedQuantity(
                        "unit:" + normalizedUnit, quantity);
        }
    }
}
