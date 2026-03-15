package com.mustafagoksal.diary;

import org.mindrot.jbcrypt.BCrypt;

public class EntryPasswordHelper {


    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }


    public static boolean verify(String plainPassword, String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) return true;
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isLocked(String storedHash) {
        return storedHash != null && !storedHash.isEmpty();
    }
}