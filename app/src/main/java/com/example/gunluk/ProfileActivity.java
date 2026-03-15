package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
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
    private Button   btnLogout;
    private Button   btnHome;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db                  = RoomDB.getInstance(this);
        tvAvatarInitial     = findViewById(R.id.tv_avatar_initial);
        tvProfileUsername   = findViewById(R.id.tv_profile_username);
        tvEntryCount        = findViewById(R.id.tv_entry_count);
        tvMemberSinceDate   = findViewById(R.id.tv_member_since_date);
        btnLogout           = findViewById(R.id.btn_logout);
        btnHome             = findViewById(R.id.btn_home);

        loadProfile();

        btnLogout.setOnClickListener(v -> confirmLogout());
        btnHome.setOnClickListener(v -> goHome());
    }

    private void loadProfile() {
        Users user = CurrentUser.getUser();
        if (user == null) return;

        String username = user.getUsername();

        tvProfileUsername.setText(username);
        if (!username.isEmpty()) {
            tvAvatarInitial.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }


        int count = db.mainDAO().getEntriesByAuthor(username).size();
        tvEntryCount.setText(String.valueOf(count));


        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvMemberSinceDate.setText(String.valueOf(year));
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
