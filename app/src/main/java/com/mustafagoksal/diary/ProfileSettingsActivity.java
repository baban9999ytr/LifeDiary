package com.mustafagoksal.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.mustafagoksal.diary.database.RoomDB;

import java.util.Locale;

public class ProfileSettingsActivity extends AppCompatActivity {

    private SwitchCompat switchGamification, switchStreaks, switchLocations;
    private LinearLayout layoutGamificationOptions;
    private Button btnEnglish, btnTurkish;

    private GamificationManager gm;
    private RoomDB.UserStats stats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_profile_settings);


        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_media_previous);
        toolbar.setNavigationOnClickListener(v -> finish());


        switchGamification        = findViewById(R.id.switch_gamification);
        switchStreaks             = findViewById(R.id.switch_streaks);
        switchLocations           = findViewById(R.id.switch_locations);
        layoutGamificationOptions = findViewById(R.id.layout_gamification_options);
        btnEnglish                = findViewById(R.id.btn_english);
        btnTurkish                = findViewById(R.id.btn_turkish);

        gm    = new GamificationManager(this, CurrentUser.getUser().getUsername());
        stats = gm.getOrCreateStats();

        switchGamification.setChecked(stats.gamificationEnabled);
        switchStreaks.setChecked(stats.streaksEnabled);
        switchLocations.setChecked(stats.locationsEnabled);
        layoutGamificationOptions.setVisibility(
                stats.gamificationEnabled ? View.VISIBLE : View.GONE);

        switchGamification.setOnCheckedChangeListener((v, checked) -> {
            stats.gamificationEnabled = checked;
            layoutGamificationOptions.setVisibility(checked ? View.VISIBLE : View.GONE);
            saveStats();
        });

        switchStreaks.setOnCheckedChangeListener((v, checked) -> {
            stats.streaksEnabled = checked;
            saveStats();
        });

        switchLocations.setOnCheckedChangeListener((v, checked) -> {
            stats.locationsEnabled = checked;
            saveStats();
        });

        highlightActiveLang();

        btnEnglish.setOnClickListener(v -> applyLocale("en"));
        btnTurkish.setOnClickListener(v -> applyLocale("tr"));
    }


    private void saveStats() {
        RoomDB.getInstance(this).mainDAO().insertOrUpdateStats(stats);
    }

    private void applyLocale(String langCode) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        prefs.edit().putString("language", langCode).apply();

        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                androidx.core.os.LocaleListCompat.create(new Locale(langCode))
        );
        highlightActiveLang();
    }

    private void highlightActiveLang() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String current = prefs.getString("language", "en");

        int activeColor   = ContextCompat.getColor(this, R.color.accent_terracotta);
        int inactiveColor = ContextCompat.getColor(this, R.color.card_bg);
        int activeText    = ContextCompat.getColor(this, R.color.warm_white);
        int inactiveText  = ContextCompat.getColor(this, R.color.ink_black);

        btnEnglish.setBackgroundColor("en".equals(current) ? activeColor : inactiveColor);
        btnEnglish.setTextColor("en".equals(current) ? activeText : inactiveText);

        btnTurkish.setBackgroundColor("tr".equals(current) ? activeColor : inactiveColor);
        btnTurkish.setTextColor("tr".equals(current) ? activeText : inactiveText);
    }
}