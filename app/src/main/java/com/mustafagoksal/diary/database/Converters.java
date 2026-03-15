package com.mustafagoksal.diary.database;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class Converters {


    private static final String DELIM = "\u0001";

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }

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


            if (i < list.size() - 1) {
                sb.append(DELIM);
            }
        }
        return sb.toString();
    }
}