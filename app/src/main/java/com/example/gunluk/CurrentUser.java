package com.example.gunluk;

import com.example.gunluk.models.Users;

/**
 * Uygulama genelinde giriş yapmış kullanıcıyı tutan statik sınıf.
 * Activity'ler arası geçişte session bilgisini taşır.
 */
public class CurrentUser {

    private static Users loggedInUser = null;

    public static void login(Users user) {
        loggedInUser = user;
    }

    public static void logout() {
        loggedInUser = null;
    }

    public static Users getUser() {
        return loggedInUser;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
