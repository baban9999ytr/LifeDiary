package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * AboutActivity — activity_about.xml
 *
 * Uygulama hakkında statik bilgi sayfası.
 * Herhangi bir DB işlemi yoktur.
 */
public class AboutActivity extends AppCompatActivity {

    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Açıklama ve geliştirici metni strings.xml'den otomatik gelir.
        // Dinamik değer eklemek istersen aşağıdaki satırları aç:
        // TextView tvDescription = findViewById(R.id.tv_about_description);
        // TextView tvDeveloper   = findViewById(R.id.tv_developer);
        // tvDescription.setText(R.string.about_description);
        // tvDeveloper.setText(R.string.about_developer);

        btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> goHome());
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
