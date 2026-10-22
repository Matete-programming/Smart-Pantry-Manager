package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Switch switchExpiryAlerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        Button btnSave = findViewById(R.id.btnSaveSettings);
        Button btnBack = findViewById(R.id.btnBackFromSettings);

        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        try (Cursor cursor = databaseHelper.getReadableDatabase()
                .rawQuery(
                        "SELECT expiry_alerts FROM settings WHERE id = 1",
                        null)) {

            if (cursor.moveToFirst()) {
                switchExpiryAlerts.setChecked(cursor.getInt(0) == 1);
            }
        }
    }

    private void saveSettings() {
        ContentValues values = new ContentValues();
        values.put(
                "expiry_alerts",
                switchExpiryAlerts.isChecked() ? 1 : 0);

        int updated = databaseHelper.getWritableDatabase().update(
                "settings",
                values,
                "id = ?",
                new String[]{"1"});

        if (updated > 0) {
            Toast.makeText(
                    this,
                    "Settings saved successfully.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                    this,
                    "Could not save settings.",
                    Toast.LENGTH_SHORT).show();
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

