package com.example.gunluk;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class FontHelper {

    public enum EntryFont {
        DEFAULT("default"),
        SERIF("serif"),
        MONO("monospace"),
        DANCING("dancing_script"),
        PLAYFAIR("playfair_display");

        public final String key;
        EntryFont(String key) { this.key = key; }

        public static EntryFont fromKey(String key) {
            for (EntryFont f : values()) if (f.key.equals(key)) return f;
            return DEFAULT;
        }
    }


    public static void applyFont(Context ctx, TextView tv, String fontKey) {
        switch (EntryFont.fromKey(fontKey)) {
            case SERIF:
                tv.setTypeface(Typeface.SERIF);
                break;
            case MONO:
                tv.setTypeface(Typeface.MONOSPACE);
                break;
            case DANCING:

                tv.setTypeface(ResourcesCompat.getFont(ctx, com.example.gunluk.R.font.dancing_script));
                break;
            case PLAYFAIR:
                tv.setTypeface(ResourcesCompat.getFont(ctx, com.example.gunluk.R.font.playfair_display));
                break;
            default:
                tv.setTypeface(Typeface.DEFAULT);
        }
    }

    public static String[] getFontLabels() {
        return new String[]{"Default", "Serif", "Monospace", "Dancing Script", "Playfair Display"};
    }

    public static String[] getFontKeys() {
        return new String[]{"default", "serif", "monospace", "dancing_script", "playfair_display"};
    }
}