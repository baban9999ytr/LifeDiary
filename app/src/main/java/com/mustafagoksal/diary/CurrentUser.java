package com.mustafagoksal.diary;

import com.mustafagoksal.diary.models.Users;


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
