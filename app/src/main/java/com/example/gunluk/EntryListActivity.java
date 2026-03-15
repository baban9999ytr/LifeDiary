package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gunluk.CurrentUser;
import com.example.gunluk.database.RoomDB;
import com.example.gunluk.models.DiaryEntry;
import com.example.gunluk.models.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * EntryListActivity — activity_entry_list.xml
 *
 * Giriş yapmış kullanıcıya ait tüm günlükleri listeler.
 * Boş durum gösterimi (layout_empty) ve FAB ile yeni girdi ekleme desteği.
 * Bir öğeye tıklamak EntryDetailActivity'yi açar.
 */
public class EntryListActivity extends AppCompatActivity
        implements EntryAdapter.OnEntryClickListener {

    private RecyclerView          recyclerEntries;
    private LinearLayout          layoutEmpty;
    private TextView              tvEntryCount;
    private Button                btnHome;
    private FloatingActionButton  fabNewEntry;

    private RoomDB       db;
    private EntryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_entry_list);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db              = RoomDB.getInstance(this);
        recyclerEntries = findViewById(R.id.recycler_entries);
        layoutEmpty     = findViewById(R.id.layout_empty);
        tvEntryCount    = findViewById(R.id.tv_entry_count);
        btnHome         = findViewById(R.id.btn_home);
        fabNewEntry     = findViewById(R.id.fab_new_entry);

        recyclerEntries.setLayoutManager(new LinearLayoutManager(this));

        fabNewEntry.setOnClickListener(v ->
            startActivity(new Intent(this, NewEntryActivity.class)));

        btnHome.setOnClickListener(v -> goHome());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEntries(); // Her dönüşte listeyi yenile (silme/ekleme sonrası)
    }

    private void loadEntries() {
        Users user = CurrentUser.getUser();
        if (user == null) {
            finish();
            return;
        }
        String username = user.getUsername();
        List<DiaryEntry> entries = db.mainDAO().getEntriesByAuthor(username);

        // Sayaç
        tvEntryCount.setText(getString(R.string.entry_count, entries.size()));

        if (entries.isEmpty()) {
            recyclerEntries.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerEntries.setVisibility(View.VISIBLE);
            adapter = new EntryAdapter(entries, this);
            recyclerEntries.setAdapter(adapter);
        }
    }

    /** RecyclerView öğe tıklaması → Detay sayfası */
    @Override
    public void onEntryClick(DiaryEntry entry) {
        Intent intent = new Intent(this, EntryDetailActivity.class);
        intent.putExtra(EntryDetailActivity.EXTRA_ENTRY_ID, entry.getID());
        startActivity(intent);
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
