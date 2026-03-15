package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.gunluk.models.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * MainActivity — activity_main.xml
 *
 * Main hub of the application.
 * Displays the username and current date.
 * Navigates to different sections via CardView buttons.
 */
public class MainActivity extends AppCompatActivity {

    private TextView  tvUsername;
    private TextView  tvDateToday;
    private CardView  cardNewEntry;
    private CardView  cardViewEntries;
    private CardView  cardProfile;
    private CardView  cardAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirect to Login if not authenticated
        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Views
        tvUsername      = findViewById(R.id.tv_username_display);
        tvDateToday     = findViewById(R.id.tv_date_today);
        cardNewEntry    = findViewById(R.id.card_new_entry);
        cardViewEntries = findViewById(R.id.card_view_entries);
        cardProfile     = findViewById(R.id.card_profile);
        cardAbout       = findViewById(R.id.card_about);

        // Display Username
        Users user = CurrentUser.getUser();
        if (user != null) {
            tvUsername.setText(user.getUsername());
        }

        // Set localized date using the string resource placeholder
        String dateString = getFormattedDate();
        tvDateToday.setText(getString(R.string.main_date_today_placeholder, dateString));

        // Navigation Click Listeners
        cardNewEntry.setOnClickListener(v ->
                startActivity(new Intent(this, NewEntryActivity.class)));

        cardViewEntries.setOnClickListener(v ->
                startActivity(new Intent(this, EntryListActivity.class)));

        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        cardAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
    }

    /** * Returns the current date formatted in English (e.g., "March 14, 2026").
     * Uses Locale.US to prevent location-based localization.
     */
    private String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        return sdf.format(new Date());
    }
}