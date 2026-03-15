package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gunluk.database.RoomDB;
import com.example.gunluk.models.Users;

public class LoginActivity extends AppCompatActivity {

    private EditText    etUsername;
    private EditText    etPassword;
    private ImageButton btnTogglePassword;
    private Button      btnLogin;
    private TextView    tvGoRegister;

    private RoomDB db;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CurrentUser.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        db                = RoomDB.getInstance(this);
        etUsername        = findViewById(R.id.et_username);
        etPassword        = findViewById(R.id.et_password);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
        btnLogin          = findViewById(R.id.btn_login);
        tvGoRegister      = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoRegister.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.login_error_empty_username));
            etUsername.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.login_error_empty_password));
            etPassword.requestFocus();
            return;
        }

        Users user = db.mainDAO().loginByUsernameOrEmail(username, password);

        if (user == null) {
            Toast.makeText(this, getString(R.string.login_error_invalid_credentials), Toast.LENGTH_SHORT).show();
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
