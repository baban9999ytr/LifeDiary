package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gunluk.database.RoomDB;
import com.example.gunluk.models.Users;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button   btnLogin;
    private TextView tvGoRegister;

    private RoomDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CurrentUser.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        db           = RoomDB.getInstance(this);
        etUsername   = findViewById(R.id.et_username);
        btnLogin     = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoRegister.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.login_error_empty_username));
            etUsername.requestFocus();
            return;
        }

        Users user = db.mainDAO().findByUsername(username);

        if (user == null) {
            etUsername.setError(getString(R.string.login_error_user_not_found));
            etUsername.requestFocus();
            return;
        }

        CurrentUser.login(user);
        Toast.makeText(this, getString(R.string.toast_welcome, user.getUsername()), Toast.LENGTH_SHORT).show();
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
