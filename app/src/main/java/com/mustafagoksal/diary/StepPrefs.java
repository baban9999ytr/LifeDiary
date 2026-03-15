package com.mustafagoksal.diary;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepPrefs {
    private static final String PREFS = "step_prefs";
    private static final String KEY_BASELINE = "step_baseline";
    private static final String KEY_DATE = "step_date";


    public static int getDailyBaseline(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String savedDate = prefs.getString(KEY_DATE, "");
        if (!today.equals(savedDate)) {

            return 0;
        }
        return prefs.getInt(KEY_BASELINE, 0);
    }

    public static void setBaseline(Context context, int steps) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_BASELINE, steps)
                .putString(KEY_DATE, today)
                .apply();
    }
}