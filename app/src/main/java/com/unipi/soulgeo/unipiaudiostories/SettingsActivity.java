package com.unipi.soulgeo.unipiaudiostories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private RadioButton rbEnglish, rbGreek, rbJapanese;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("AppConfig", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        super.attachBaseContext(Utilities.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rbEnglish = findViewById(R.id.rbEnglish);
        rbGreek = findViewById(R.id.rbGreek);
        rbJapanese = findViewById(R.id.rbJapanese);

        Button btnSave = findViewById(R.id.btnSave);

        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);

        String lang = prefs.getString("My_Lang", "en");
        if (lang.equals("el")) rbGreek.setChecked(true);
        else if (lang.equals("ja")) rbJapanese.setChecked(true);
        else rbEnglish.setChecked(true);
    }

    private void saveSettings() {
        String langCode = "en";

        if (rbGreek.isChecked()) langCode = "el";
        else if (rbJapanese.isChecked()) langCode = "ja";

        SharedPreferences.Editor editor = getSharedPreferences("AppConfig", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langCode);
        editor.apply();

        // RESTART APP to apply changes
        Intent intent = new Intent(this, MainActivity.class);
        // Clear the back stack so the user can't go back to the old language
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}

