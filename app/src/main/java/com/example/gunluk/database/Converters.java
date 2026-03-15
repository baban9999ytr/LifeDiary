package com.example.gunluk.database;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Room TypeConverters for storing List<String> as a single String.
 * Uses a delimiter approach for better performance over JSON.
 */
public final class Converters {

    // Delimiter unlikely to appear in URIs or file paths
    private static final String DELIM = "\u0001";

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        // Using -1 in split preserves empty strings if they exist between delimiters
        return new ArrayList<>(Arrays.asList(value.split(DELIM, -1)));
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            sb.append(item != null ? item : "");

            // Only add delimiter between items
            if (i < list.size() - 1) {
                sb.append(DELIM);
            }
        }
        return sb.toString();
    }
}