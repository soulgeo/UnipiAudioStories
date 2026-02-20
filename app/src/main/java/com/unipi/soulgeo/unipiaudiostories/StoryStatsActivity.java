package com.unipi.soulgeo.unipiaudiostories;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StoryStatsActivity extends AppCompatActivity {

    private LinearLayout statsContainer;

    // Helper class to hold data for sorting
    private static class StatItem implements Comparable<StatItem> {
        String title;
        int count;

        public StatItem(String title, int count) {
            this.title = title;
            this.count = count;
        }

        // Sort descending (highest count first)
        @Override
        public int compareTo(StatItem other) {
            return Integer.compare(other.count, this.count);
        }
    }

    // Keep Language Settings
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("AppConfig", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        super.attachBaseContext(Utilities.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        statsContainer = findViewById(R.id.statsContainer);

        loadStatistics();
    }

    private void loadStatistics() {
        SharedPreferences prefs = getSharedPreferences("StoryStats", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        List<StatItem> statsList = new ArrayList<>();

        // 1. Extract data
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("count_")) {
                // Remove "count_" prefix to get the real title
                String cleanTitle = key.replace("count_", "");
                int value = Integer.parseInt(entry.getValue().toString());
                statsList.add(new StatItem(cleanTitle, value));
            }
        }

        // 2. Sort by plays (Highest first)
        Collections.sort(statsList);

        // 3. Display
        if (statsList.isEmpty()) {
            showEmptyState();
        } else {
            populateList(statsList);
        }
    }

    private void populateList(List<StatItem> list) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < list.size(); i++) {
            StatItem item = list.get(i);

            // Create a new row from XML
            View rowView = inflater.inflate(R.layout.item_stat, statsContainer, false);

            // Find views inside that row
            TextView tvRank = rowView.findViewById(R.id.tvRank);
            TextView tvTitle = rowView.findViewById(R.id.tvStatTitle);
            TextView tvCount = rowView.findViewById(R.id.tvCount);

            // Set data
            tvRank.setText((i + 1) + "."); // 1., 2., 3.
            tvTitle.setText(item.title);
            tvCount.setText(item.count + " plays");

            // Add to the screen
            statsContainer.addView(rowView);
        }
    }

    private void showEmptyState() {
        TextView emptyTv = new TextView(this);
        emptyTv.setText("No stories listened to yet!");
        emptyTv.setTextSize(18);
        emptyTv.setPadding(16, 16, 16, 16);
        statsContainer.addView(emptyTv);
    }
}
