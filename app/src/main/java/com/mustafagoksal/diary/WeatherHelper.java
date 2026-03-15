package com.mustafagoksal.diary;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHelper {

    public interface WeatherCallback {
        void onWeather(String description, String iconCode);
        void onFailed(String reason);
    }

    private static String wmoToLabel(int code) {
        if (code == 0)       return "Clear sky";
        if (code <= 3)       return "Partly cloudy";
        if (code <= 49)      return "Foggy";
        if (code <= 59)      return "Drizzle";
        if (code <= 69)      return "Rain";
        if (code <= 79)      return "Snow";
        if (code <= 82)      return "Rain showers";
        if (code <= 86)      return "Snow showers";
        if (code <= 99)      return "Thunderstorm";
        return "Unknown";
    }

    public static void fetch(double lat, double lng, WeatherCallback callback) {
        new Thread(() -> {
            try {
                String urlStr = "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + lat
                        + "&longitude=" + lng
                        + "&current_weather=true"
                        + "&temperature_unit=celsius";

                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    postFailed(callback, "HTTP " + responseCode);
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject root = new JSONObject(sb.toString());
                JSONObject cw   = root.getJSONObject("current_weather");
                double temp     = cw.getDouble("temperature");
                int wmoCode     = cw.getInt("weathercode");

                String description = wmoToLabel(wmoCode) + ", " + (int) temp + "°C";
                String iconCode    = String.valueOf(wmoCode);

                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onWeather(description, iconCode));

            } catch (Exception e) {
                postFailed(callback, e.getMessage());
            }
        }).start();
    }

    private static void postFailed(WeatherCallback callback, String reason) {
        new Handler(Looper.getMainLooper())
                .post(() -> callback.onFailed(reason));
    }
}