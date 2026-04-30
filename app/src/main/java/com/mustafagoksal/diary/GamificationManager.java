package com.mustafagoksal.diary;

import android.content.Context;

import com.mustafagoksal.diary.database.RoomDB;
import com.mustafagoksal.diary.models.UserStats;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GamificationManager {

    private final RoomDB db;
    private final String username;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public GamificationManager(Context ctx, String username) {
        this.db       = RoomDB.getInstance(ctx);
        this.username = username != null ? username : "";
    }

    public UserStats getOrCreateStats() {
        UserStats stats = db.mainDAO().getStatsByUsername(username);
        if (stats == null) {
            stats = new UserStats();
            stats.username = username;
            db.mainDAO().insertOrUpdateStats(stats);
        }
        return stats;
    }


    public void onEntryAddedWithStats(String plainText) {
        UserStats stats = getOrCreateStats();
        if (!stats.gamificationEnabled) return;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        String currentWeekStr = year + "-W" + week;

        if (stats.currentWeek == null || !stats.currentWeek.equals(currentWeekStr)) {
            stats.currentWeek = currentWeekStr;
            stats.wordsThisWeek = 0;
            stats.charsThisWeek = 0;
        }

        if (plainText != null && !plainText.isEmpty()) {
            stats.charsThisWeek += plainText.length();
            String[] words = plainText.trim().split("\\s+");
            if (words.length > 0 && !words[0].isEmpty()) {
                stats.wordsThisWeek += words.length;
            }
        }

        String today = sdf.format(new Date());
        if (today.equals(stats.lastEntryDate)) {
                 db.mainDAO().insertOrUpdateStats(stats);
            return;
        }

        if (stats.streaksEnabled) {
            String yesterday = shiftDay(today, -1);
            if (yesterday.equals(stats.lastEntryDate)) {
                stats.currentStreak += 1;
            } else {
                stats.currentStreak = 1;
            }
            stats.longestStreak = Math.max(stats.longestStreak, stats.currentStreak);
        }

        stats.lastEntryDate = today;

        String yearStr = today.substring(0, 4);
        stats.daysWrittenThisYear = db.mainDAO().getDistinctDaysForYear(username, yearStr).size();

        db.mainDAO().insertOrUpdateStats(stats);
    }



    private String shiftDay(String dateStr, int days) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dateStr));
            cal.add(Calendar.DAY_OF_YEAR, days);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }
}