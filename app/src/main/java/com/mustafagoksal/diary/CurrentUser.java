package com.mustafagoksal.diary;

import android.content.Context;
import android.content.SharedPreferences;

import com.mustafagoksal.diary.database.RoomDB;
import com.mustafagoksal.diary.models.Users;

public class CurrentUser {

    private static final String PREFS_NAME = "session";
    private static final String KEY_USERNAME = "logged_in_user";

    private static Users loggedInUser = null;
    private static Context appContext = null;


    public static void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
    }

    public static void login(Users user) {
        loggedInUser = user;
        if (appContext != null && user != null) {
            appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_USERNAME, user.getUsername())
                    .apply();
        }
    }

    public static void logout() {
        loggedInUser = null;
        if (appContext != null) {
            appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .remove(KEY_USERNAME)
                    .apply();
        }
    }

    public static Users getUser() {
        if (loggedInUser != null) return loggedInUser;

        if (appContext != null) {
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedUsername = prefs.getString(KEY_USERNAME, null);
            if (savedUsername != null && !savedUsername.isEmpty()) {
                try {
                    Users restored = RoomDB.getInstance(appContext).mainDAO().findByUsername(savedUsername);
                    if (restored != null) {
                        loggedInUser = restored;
                        return loggedInUser;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    public static boolean isLoggedIn() {
        return getUser() != null;
    }
}
