package com.mustafagoksal.diary;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

public class AboutActivity extends AppCompatActivity {

    private Button btnHomeOriginal;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());


        nestedScrollView = findViewById(R.id.nested_scroll_view);
        btnHomeOriginal = findViewById(R.id.btn_home);
        FloatingActionButton btnMapstobottom = findViewById(R.id.Mapstobottom);


        findViewById(R.id.btn_github).setOnClickListener(v -> openUrl("https://github.com/baban9999ytr"));
        findViewById(R.id.btn_linkedin).setOnClickListener(v -> openUrl("https://www.linkedin.com/in/mustafa-goksal-ari/"));
        findViewById(R.id.btn_kreosus).setOnClickListener(v -> openUrl("https://kreosus.com/aridigital/about"));

        btnHomeOriginal.setOnClickListener(v -> goHome());

        btnMapstobottom.setOnClickListener(v -> {
            nestedScrollView.post(() -> nestedScrollView.fullScroll(View.FOCUS_DOWN));
        });

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    Rect scrollBounds = new Rect();
                    nestedScrollView.getHitRect(scrollBounds);

                    if (btnHomeOriginal.getLocalVisibleRect(scrollBounds)) {
                        if (btnMapstobottom.getVisibility() == View.VISIBLE) {
                            btnMapstobottom.animate().alpha(0.0f).setDuration(200).withEndAction(() -> btnMapstobottom.setVisibility(View.GONE));
                        }
                    } else {
                        if (btnMapstobottom.getVisibility() == View.GONE || btnMapstobottom.getVisibility() == View.INVISIBLE) {
                            btnMapstobottom.setAlpha(0.0f);
                            btnMapstobottom.setVisibility(View.VISIBLE);
                            btnMapstobottom.animate().alpha(1.0f).setDuration(200);
                        }
                    }
                });
    }


    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}