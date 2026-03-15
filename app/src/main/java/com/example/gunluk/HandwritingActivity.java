package com.example.gunluk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;

public class HandwritingActivity extends AppCompatActivity {

    public static final String EXTRA_HW_PATH = "hw_path";

    private HandwritingView handwritingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        handwritingView = findViewById(R.id.handwriting_view);

        ImageButton btnUndo = findViewById(R.id.btn_undo);
        ImageButton btnClear = findViewById(R.id.btn_clear);
        Button btnDone = findViewById(R.id.btn_done);

        btnUndo.setOnClickListener(v -> handwritingView.undo());
        btnClear.setOnClickListener(v -> handwritingView.clear());
        btnDone.setOnClickListener(v -> saveAndFinish());

        // Color buttons
        findViewById(R.id.btn_pen_black).setOnClickListener(v -> handwritingView.setPenColor(Color.parseColor("#000000")));
        findViewById(R.id.btn_pen_brown).setOnClickListener(v -> handwritingView.setPenColor(Color.parseColor("#5C4033")));
        findViewById(R.id.btn_pen_blue).setOnClickListener(v -> handwritingView.setPenColor(Color.parseColor("#1E88E5")));
        findViewById(R.id.btn_pen_red).setOnClickListener(v -> handwritingView.setPenColor(Color.parseColor("#E53935")));
        
        findViewById(R.id.btn_eraser).setOnClickListener(v -> handwritingView.setEraser(true));
    }

    private void saveAndFinish() {
        if (handwritingView.isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Bitmap bitmap = handwritingView.exportBitmap();
        if (bitmap == null) {
            Toast.makeText(this, "Failed to capture drawing", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File dir = new File(getFilesDir(), "handwriting");
            if (!dir.exists() && !dir.mkdirs()) {
                Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                return;
            }

            String filename = "hw_" + System.currentTimeMillis() + ".png";
            File file = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_HW_PATH, file.getAbsolutePath());
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error saving handwriting", Toast.LENGTH_SHORT).show();
        }
    }
}
