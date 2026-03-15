package com.example.gunluk;

import org.mindrot.jbcrypt.BCrypt;

public class EntryPasswordHelper {

    /** Hash a plain password for storage. */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /** Check a plain password against the stored hash. */
    public static boolean verify(String plainPassword, String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) return true; // no password set
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