package com.example.gunluk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gunluk.database.RoomDB;
import com.example.gunluk.models.Users;

/**
 * RegisterActivity — activity_register.xml
 *
 * Yeni kullanıcı kaydı oluşturur.
 * - Kullanıcı adı: zorunlu, benzersiz olmalı
 * - E-posta: opsiyonel
 * - Şifre: opsiyonel (şifresiz hesap açılabilir)
 * - Şifre tekrar: şifre girilmişse eşleşmeli
 *
 * Başarılı kayıt sonrası LoginActivity'ye yönlendirir.
 */
public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText    etUsername;
    private EditText    etEmail;
    private EditText    etPassword;
    private EditText    etPasswordConfirm;
    private ImageButton btnTogglePassword;
    private TextView    tvError;
    private Button      btnRegister;
    private TextView    tvGoLogin;

    private RoomDB  db;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db                = RoomDB.getInstance(this);
        btnBack           = findViewById(R.id.btn_back);
        etUsername        = findViewById(R.id.et_reg_username);
        etEmail           = findViewById(R.id.et_reg_email);
        etPassword        = findViewById(R.id.et_reg_password);
        etPasswordConfirm = findViewById(R.id.et_reg_password_confirm);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
        tvError           = findViewById(R.id.tv_register_error);
        btnRegister       = findViewById(R.id.btn_register);
        tvGoLogin         = findViewById(R.id.tv_go_login);

        btnBack.setOnClickListener(v -> finish());

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoLogin.setOnClickListener(v -> {
            // Giriş ekranına dön (back stack'ten açılmışsa finish yeterli)
            finish();
        });
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        // İmleci sona taşı
        etPassword.setSelection(etPassword.getText().length());
        etPasswordConfirm.setSelection(etPasswordConfirm.getText().length());
    }

    private void attemptRegister() {
        hideError();

        String username  = etUsername.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString();
        String confirm   = etPasswordConfirm.getText().toString();

        // ── Doğrulamalar ──────────────────────────────────────────

        if (TextUtils.isEmpty(username)) {
            showError(getString(R.string.register_error_username_empty));
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            showError(getString(R.string.register_error_username_short));
            etUsername.requestFocus();
            return;
        }

        if (username.contains(" ")) {
            showError(getString(R.string.register_error_username_no_spaces));
            etUsername.requestFocus();
            return;
        }

        if (db.mainDAO().usernameExists(username) > 0) {
            showError(getString(R.string.register_error_username_taken));
            etUsername.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.register_error_email_invalid));
            etEmail.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(password) && password.length() < 4) {
            showError(getString(R.string.register_error_password_short));
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            showError(getString(R.string.register_error_password_mismatch));
            etPasswordConfirm.requestFocus();
            return;
        }

        // ── Kaydet ───────────────────────────────────────────────

        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setProfilePhotoUri("");

        db.mainDAO().insertUser(newUser);

        Toast.makeText(this, getString(R.string.toast_register_success), Toast.LENGTH_LONG).show();

        // Login ekranına geri dön
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        tvError.setText("");
    }
}
