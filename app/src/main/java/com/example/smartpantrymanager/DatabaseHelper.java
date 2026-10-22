package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME =
            "smart_pantry.db";

    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY =
            "pantry_items";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void seedRecipes(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            addRecipe(db, "Tomato Omelette",
                    "Beat the eggs, chop the tomatoes, and cook together in a lightly oiled pan.",
                    new String[][] {
                            {"eggs", "2", "pieces"},
                            {"tomatoes", "2", "pieces"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Cheese Omelette",
                    "Beat the eggs, add grated cheese, and cook in a pan until set.",
                    new String[][] {
                            {"eggs", "2", "pieces"},
                            {"cheese", "50", "g"}
                    });

            addRecipe(db, "Scrambled Eggs",
                    "Beat the eggs and stir continuously in a pan until cooked.",
                    new String[][] {
                            {"eggs", "2", "pieces"},
                            {"milk", "30", "ml"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Tomato Pasta",
                    "Cook the pasta. Chop and cook the tomatoes, then combine.",
                    new String[][] {
                            {"pasta", "200", "g"},
                            {"tomatoes", "3", "pieces"},
                            {"garlic", "2", "pieces"}
                    });

            addRecipe(db, "Garlic Pasta",
                    "Cook the pasta and toss with chopped garlic and oil.",
                    new String[][] {
                            {"pasta", "200", "g"},
                            {"garlic", "2", "pieces"},
                            {"olive oil", "2", "tbsp"}
                    });

            addRecipe(db, "Cheese Pasta",
                    "Cook the pasta, stir in the milk, and add grated cheese.",
                    new String[][] {
                            {"pasta", "200", "g"},
                            {"cheese", "80", "g"},
                            {"milk", "100", "ml"}
                    });

            addRecipe(db, "Banana Pancakes",
                    "Mash the banana, mix with eggs and flour, then cook small pancakes.",
                    new String[][] {
                            {"banana", "1", "pieces"},
                            {"eggs", "2", "pieces"},
                            {"flour", "100", "g"}
                    });

            addRecipe(db, "Basic Pancakes",
                    "Whisk flour, milk, and eggs into a batter. Cook in a lightly oiled pan.",
                    new String[][] {
                            {"flour", "150", "g"},
                            {"milk", "250", "ml"},
                            {"eggs", "2", "pieces"}
                    });

            addRecipe(db, "French Toast",
                    "Dip the bread in beaten egg and milk, then fry until golden.",
                    new String[][] {
                            {"bread", "2", "pieces"},
                            {"eggs", "1", "pieces"},
                            {"milk", "50", "ml"}
                    });

            addRecipe(db, "Cheese Toast",
                    "Place cheese on bread and toast until the cheese melts.",
                    new String[][] {
                            {"bread", "2", "pieces"},
                            {"cheese", "50", "g"}
                    });

            addRecipe(db, "Tomato Sandwich",
                    "Slice the tomatoes, place them on bread, and season.",
                    new String[][] {
                            {"bread", "2", "pieces"},
                            {"tomatoes", "1", "pieces"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Potato Mash",
                    "Boil potatoes until soft, mash with milk, and season.",
                    new String[][] {
                            {"potatoes", "300", "g"},
                            {"milk", "50", "ml"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Roasted Potatoes",
                    "Cut potatoes into pieces, toss with oil, and roast until tender.",
                    new String[][] {
                            {"potatoes", "300", "g"},
                            {"olive oil", "2", "tbsp"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Carrot Soup",
                    "Chop the carrots, simmer in water until soft, then blend.",
                    new String[][] {
                            {"carrots", "250", "g"},
                            {"onion", "1", "pieces"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Vegetable Stir-Fry",
                    "Chop the vegetables and stir-fry until tender.",
                    new String[][] {
                            {"carrots", "100", "g"},
                            {"broccoli", "100", "g"},
                            {"olive oil", "1", "tbsp"}
                    });

            addRecipe(db, "Garlic Potatoes",
                    "Cook the potatoes and toss with garlic and olive oil.",
                    new String[][] {
                            {"potatoes", "300", "g"},
                            {"garlic", "2", "pieces"},
                            {"olive oil", "1", "tbsp"}
                    });

            addRecipe(db, "Banana Smoothie",
                    "Blend the banana and milk until smooth.",
                    new String[][] {
                            {"banana", "1", "pieces"},
                            {"milk", "250", "ml"}
                    });

            addRecipe(db, "Apple Smoothie",
                    "Core the apple and blend it with milk.",
                    new String[][] {
                            {"apples", "1", "pieces"},
                            {"milk", "200", "ml"}
                    });

            addRecipe(db, "Simple Rice Bowl",
                    "Cook the rice according to the packet instructions and season.",
                    new String[][] {
                            {"rice", "150", "g"},
                            {"salt", "1", "tsp"}
                    });

            addRecipe(db, "Carrot Salad",
                    "Grate the carrots and mix with a little olive oil.",
                    new String[][] {
                            {"carrots", "150", "g"},
                            {"olive oil", "1", "tbsp"}
                    });

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String[][] ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insertWithOnConflict(
                "recipes",
                null,
                recipeValues,
                SQLiteDatabase.CONFLICT_IGNORE);

        // Skip ingredients if this recipe already exists.
        if (recipeId == -1) {
            return;
        }

        for (String[] ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("ingredient_name", ingredient[0]);
            ingredientValues.put("quantity", Double.parseDouble(ingredient[1]));
            ingredientValues.put("unit", ingredient[2]);

            db.insertOrThrow(
                    "recipe_ingredients",
                    null,
                    ingredientValues);
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Store the user's pantry ingredients.
        db.execSQL(
                "CREATE TABLE pantry_items (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT)"
        );

        // Store recipe names and preparation instructions.
        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL UNIQUE, " +
                        "instructions TEXT NOT NULL)"
        );

        // Store ingredients and quantities required by recipes.
        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) " +
                        "ON DELETE CASCADE)"
        );

        // Store application preferences.
        db.execSQL(
                "CREATE TABLE settings (" +
                        "id INTEGER PRIMARY KEY, " +
                        "expiry_alerts INTEGER NOT NULL DEFAULT 1)"
        );

        db.execSQL(
                "INSERT INTO settings (id, expiry_alerts) " +
                        "VALUES (1, 1)"

        );
        seedRecipes(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            seedRecipes(db);
        }
    }

    // CREATE: Add a pantry ingredient.
    public long addPantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(quantity)
                || quantity <= 0) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull(COL_EXPIRY);
        } else {
            values.put(COL_EXPIRY, expiryDate.trim());
        }

        SQLiteDatabase db = getWritableDatabase();

        return db.insert(TABLE_PANTRY, null, values);
    }

    // READ: Retrieve pantry ingredients.
    // The caller must close the returned Cursor.
    public Cursor getAllPantryItems() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " COLLATE NOCASE ASC"
        );
    }

    // UPDATE: Edit an existing pantry ingredient.
    public int updatePantryItem(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        if (id <= 0
                || name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(quantity)
                || quantity <= 0) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull(COL_EXPIRY);
        } else {
            values.put(COL_EXPIRY, expiryDate.trim());
        }

        SQLiteDatabase db = getWritableDatabase();

        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // DELETE: Remove a pantry ingredient.
    public int deletePantryItem(int id) {

        if (id <= 0) {
            return 0;
        }

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public Cursor getPantryItemById(int id) {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

}

