package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "ingredient_id";

    private DatabaseHelper databaseHelper;

    private EditText edtName;
    private EditText edtQuantity;
    private EditText edtExpiry;
    private Spinner spinnerUnit;
    private TextView txtTitle;

    private int itemId = -1;

    private final String[] units = {
            "Select unit", "pieces", "g", "kg",
            "ml", "l", "cups", "tbsp", "tsp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        txtTitle = findViewById(R.id.txtFormTitle);
        edtName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtExpiry = findViewById(R.id.edtExpiryDate);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);

        if (itemId != -1) {
            txtTitle.setText("Edit Ingredient");
            btnSave.setText("Update Ingredient");
            loadIngredient();
        }

        btnSave.setOnClickListener(v -> saveIngredient());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadIngredient() {

        try (Cursor cursor =
                     databaseHelper.getPantryItemById(itemId)) {

            if (!cursor.moveToFirst()) {
                Toast.makeText(
                        this,
                        "Ingredient not found.",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
                return;
            }

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COL_NAME));

            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COL_QUANTITY));

            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COL_UNIT));

            int expiryColumn = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COL_EXPIRY);

            String expiry = cursor.isNull(expiryColumn)
                    ? ""
                    : cursor.getString(expiryColumn);

            edtName.setText(name);
            edtQuantity.setText(
                    String.format(Locale.US, "%s", quantity));
            edtExpiry.setText(expiry);

            for (int i = 0; i < units.length; i++) {
                if (units[i].equalsIgnoreCase(unit)) {
                    spinnerUnit.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveIngredient() {

        String name = edtName.getText().toString().trim();
        String quantityText =
                edtQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = edtExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Enter an ingredient name.");
            edtName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            edtQuantity.setError("Enter a quantity.");
            edtQuantity.requestFocus();
            return;
        }

        final double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid quantity.");
            edtQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            edtQuantity.setError(
                    "Quantity must be a positive number.");
            edtQuantity.requestFocus();
            return;
        }

        if (spinnerUnit.getSelectedItemPosition() == 0) {
            Toast.makeText(
                    this,
                    "Please select a unit.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!expiry.isEmpty()) {
            try {
                LocalDate.parse(expiry);
            } catch (DateTimeParseException e) {
                edtExpiry.setError(
                        "Use a valid date in YYYY-MM-DD format.");
                edtExpiry.requestFocus();
                return;
            }
        }

        long result;

        if (itemId == -1) {
            result = databaseHelper.addPantryItem(
                    name, quantity, unit, expiry);
        } else {
            result = databaseHelper.updatePantryItem(
                    itemId, name, quantity, unit, expiry);
        }

        if (result > 0) {
            Toast.makeText(
                    this,
                    itemId == -1
                            ? "Ingredient added successfully."
                            : "Ingredient updated successfully.",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();

        } else {
            Toast.makeText(
                    this,
                    "Could not save the ingredient. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
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
