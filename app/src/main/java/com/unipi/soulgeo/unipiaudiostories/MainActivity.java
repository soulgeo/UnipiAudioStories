package com.unipi.soulgeo.unipiaudiostories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private StoryAdapter adapter;
    private List<Story> storyList;
    private FirebaseFirestore db;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Get Language from SharedPreferences
        SharedPreferences prefs = newBase.getSharedPreferences("AppConfig", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en"); // Default English

        Context context = Utilities.setLocale(newBase, lang);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        ImageButton btnStats = findViewById(R.id.btnStats);
        btnStats.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StoryStatsActivity.class));
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        storyList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StoryAdapter(storyList, story -> {
            // Create Intent to go to Details Page
            Intent intent = new Intent(MainActivity.this, StoryDetailsActivity.class);

            // Pass the data field by field
            intent.putExtra("sid", story.getSid());
            intent.putExtra("title", story.getTitle());
            intent.putExtra("text", story.getText());
            intent.putExtra("author", story.getAuthor());
            intent.putExtra("image", story.getImageUrl());

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadStories();
    }

    private void loadStories() {
        db.collection("stories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storyList.clear(); // Clear list to avoid duplicates
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // This converts the JSON directly into your Story object
                            Story story = document.toObject(Story.class);
                            storyList.add(story);
                        }
                        adapter.notifyDataSetChanged(); // Refresh the list
                    } else {
                        Toast.makeText(this, "Error getting stories.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
