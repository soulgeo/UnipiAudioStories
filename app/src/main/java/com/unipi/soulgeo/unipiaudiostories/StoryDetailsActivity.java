package com.unipi.soulgeo.unipiaudiostories; // Updated Package Name

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class StoryDetailsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private FloatingActionButton fabPlay;
    private boolean isSpeaking = false;

    private String storyTitle;
    private String storyText;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Keep the language logic (it's required for the UI)
        SharedPreferences prefs = newBase.getSharedPreferences("AppConfig", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        // Ensure you have copied the Utilities/LanguageManager class to this project too!
        super.attachBaseContext(Utilities.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgHeader = findViewById(R.id.imgHeader);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvText = findViewById(R.id.tvText);
        fabPlay = findViewById(R.id.fabPlay);

        // Get Data
        storyTitle = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String imageUrl = getIntent().getStringExtra("image");
        storyText = getIntent().getStringExtra("text");

        // Set Data
        tvTitle.setText(storyTitle);
        tvAuthor.setText(author);
        tvText.setText(storyText);

        // Load Image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imgHeader);
        }

        // Init TTS
        tts = new TextToSpeech(this, this);

        fabPlay.setOnClickListener(v -> toggleReading());
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to English
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    private void toggleReading() {
        if (storyText == null || storyText.isEmpty()) {
            Toast.makeText(this, "No text to read!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isSpeaking) {
            // STOP
            tts.stop();
            fabPlay.setImageResource(android.R.drawable.ic_media_play);
            isSpeaking = false;
        } else {
            // START
            tts.speak(storyText, TextToSpeech.QUEUE_FLUSH, null, null);
            fabPlay.setImageResource(android.R.drawable.ic_media_pause);
            isSpeaking = true;

            // SAVE STATS LOCALLY
            incrementStoryCount();
        }
    }

    private void incrementStoryCount() {
        // We use a separate SharedPreferences file for stats to keep it clean
        SharedPreferences statsPrefs = getSharedPreferences("StoryStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = statsPrefs.edit();

        String key = "count_" + storyTitle;
        int currentCount = statsPrefs.getInt(key, 0);

        editor.putInt(key, currentCount + 1);
        editor.apply();

        Log.d("Stats", "Incremented count for: " + storyTitle + " to " + (currentCount + 1));
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
