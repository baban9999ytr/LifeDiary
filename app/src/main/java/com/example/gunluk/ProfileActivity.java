package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gunluk.CurrentUser;
import com.example.gunluk.database.RoomDB;
import com.example.gunluk.models.Users;

import java.util.Calendar;


public class ProfileActivity extends AppCompatActivity {

    private TextView tvAvatarInitial;
    private TextView tvProfileUsername;
    private TextView tvEntryCount;
    private TextView tvMemberSinceDate;
    private View layoutGamificationPanel;
    private TextView tvStreak, tvDaysThisYear, tvLocations;
    private TextView tvWordsThisWeek, tvCharsThisWeek;
    private Button   btnLogout;
    private Button   btnHome;
    private GamificationManager gm;
    private RoomDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_profile);

        // 1. Initialize Objects
        db = RoomDB.getInstance(this);
        gm = new GamificationManager(this, CurrentUser.getUser().getUsername()); // Initialize your manager

        // 2. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 3. Bind Views (Standard)
        tvAvatarInitial     = findViewById(R.id.tv_avatar_initial);
        tvProfileUsername   = findViewById(R.id.tv_profile_username);
        tvEntryCount        = findViewById(R.id.tv_entry_count);
        tvMemberSinceDate   = findViewById(R.id.tv_member_since_date);
        btnLogout           = findViewById(R.id.btn_logout);
        btnHome             = findViewById(R.id.btn_home);
        Button btnSettings  = findViewById(R.id.btn_settings); // NEW

        // 4. Bind Views (Gamification) - Ensure these IDs exist in your XML!
        layoutGamificationPanel = findViewById(R.id.layout_gamification_panel);
        tvStreak                = findViewById(R.id.tv_streak);
        tvDaysThisYear          = findViewById(R.id.tv_days_this_year);
        tvLocations             = findViewById(R.id.tv_locations);
        tvWordsThisWeek         = findViewById(R.id.tv_words_this_week);
        tvCharsThisWeek         = findViewById(R.id.tv_chars_this_week);

        loadProfile();

        btnLogout.setOnClickListener(v -> confirmLogout());
        btnHome.setOnClickListener(v -> goHome());
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, ProfileSettingsActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile(); // Refresh stats when returning from Settings
    }

    private void loadProfile() {
        Users user = CurrentUser.getUser();
        if (user == null) return;

        // Basic Profile Info
        String username = user.getUsername();
        tvProfileUsername.setText(username);
        if (!username.isEmpty()) {
            tvAvatarInitial.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }

        // Database Stats
        java.util.List<com.example.gunluk.models.DiaryEntry> entries = db.mainDAO().getEntriesByAuthor(username);
        int count = entries.size();
        tvEntryCount.setText(String.valueOf(count));

        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (count > 0) {
            String oldestDate = entries.get(entries.size() - 1).getDate();
            if (oldestDate != null) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d{4}").matcher(oldestDate);
                if (m.find()) {
                    try { year = Integer.parseInt(m.group()); } catch (Exception ignored) {}
                }
            }
        }
        tvMemberSinceDate.setText(String.valueOf(year));

        // Gamification Logic
        // (Assuming getOrCreateStats() is a method in your GamificationManager)
        RoomDB.UserStats stats = gm.getOrCreateStats();

        if (stats != null && stats.gamificationEnabled) {
            layoutGamificationPanel.setVisibility(View.VISIBLE);

            if (stats.streaksEnabled) {
                tvStreak.setText(stats.currentStreak + " 🔥");
            }

            tvDaysThisYear.setText(String.valueOf(stats.daysWrittenThisYear));
            tvWordsThisWeek.setText(String.valueOf(stats.wordsThisWeek));
            tvCharsThisWeek.setText(String.valueOf(stats.charsThisWeek));

            if (stats.locationsEnabled) {
                tvLocations.setText(String.valueOf(gm.getVisitedLocations().size()));
            }
        } else {
            layoutGamificationPanel.setVisibility(View.GONE);
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.logout_confirm_yes, (dialog, which) -> logout())
            .setNegativeButton(R.string.logout_confirm_no, null)
            .show();
    }

    private void logout() {
        CurrentUser.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
