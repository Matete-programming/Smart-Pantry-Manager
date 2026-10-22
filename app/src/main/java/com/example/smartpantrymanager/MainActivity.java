package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;



public class MainActivity extends AppCompatActivity
        implements PantryAdapter.OnIngredientActionListener {

    private DatabaseHelper databaseHelper;
    private PantryAdapter adapter;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private RecyclerView recyclerPantry;
    private TextView txtPantryCount;
    private TextView txtEmptyPantry;
    private ActivityResultLauncher<Intent> ingredientFormLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ingredientFormLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadPantryItems();
                    }
                }
        );


        databaseHelper = new DatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtPantryCount = findViewById(R.id.txtPantryCount);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new PantryAdapter(pantryItems, this);
        recyclerPantry.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAddIngredient);
        Button btnRecipes = findViewById(R.id.btnSuggestedRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class);

            ingredientFormLauncher.launch(intent);
        });


        btnRecipes.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Recipe suggestions coming soon.",
                        Toast.LENGTH_SHORT
                ).show());

        btnSettings.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Settings screen coming soon.",
                        Toast.LENGTH_SHORT
                ).show());

        loadPantryItems();
    }

    private void loadPantryItems() {

        pantryItems.clear();

        try (Cursor cursor = databaseHelper.getAllPantryItems()) {

            int idColumn = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_ID);

            int nameColumn = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_NAME);

            int quantityColumn = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_QUANTITY);

            int unitColumn = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_UNIT);

            int expiryColumn = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_EXPIRY);

            while (cursor.moveToNext()) {

                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                double quantity = cursor.getDouble(quantityColumn);
                String unit = cursor.getString(unitColumn);

                String expiry = cursor.isNull(expiryColumn)
                        ? null
                        : cursor.getString(expiryColumn);

                pantryItems.add(new PantryItem(
                        id, name, quantity, unit, expiry));
            }
        }

        adapter.notifyDataSetChanged();

        txtPantryCount.setText(
                "Ingredients saved: " + pantryItems.size());

        txtEmptyPantry.setVisibility(
                pantryItems.isEmpty()
                        ? TextView.VISIBLE
                        : TextView.GONE);

        recyclerPantry.setVisibility(
                pantryItems.isEmpty()
                        ? RecyclerView.GONE
                        : RecyclerView.VISIBLE);
    }


    @Override
    public void onEdit(PantryItem item) {

        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class);

        intent.putExtra(
                AddEditIngredientActivity.EXTRA_ITEM_ID,
                item.getId());

        ingredientFormLauncher.launch(intent);
    }


    @Override
    public void onDelete(PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getName() + "?")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Delete", (dialog, which) -> {

                    int deleted = databaseHelper.deletePantryItem(
                            item.getId());

                    if (deleted > 0) {
                        Toast.makeText(
                                this,
                                "Ingredient deleted.",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadPantryItems();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not delete ingredient.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null) {
            loadPantryItems();
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}
