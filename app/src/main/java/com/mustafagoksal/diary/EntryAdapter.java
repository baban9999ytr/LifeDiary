package com.mustafagoksal.diary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafagoksal.diary.models.DiaryEntry;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    public interface OnEntryClickListener {
        void onEntryClick(DiaryEntry entry);
    }

    private final List<DiaryEntry>     entries;
    private final OnEntryClickListener listener;

    // Shared thread pool for background image decoding
    private static final ExecutorService executor  = Executors.newFixedThreadPool(3);
    private static final Handler         mainHandler = new Handler(Looper.getMainLooper());

    public EntryAdapter(List<DiaryEntry> entries, OnEntryClickListener listener) {
        this.entries  = entries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    class EntryViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivEntryCover;
        private final TextView  tvDayNumber;
        private final TextView  tvMonthShort;
        private final TextView  tvEntryTitle;
        private final TextView  tvEntryPreview;


        private String currentImagePath = null;

        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEntryCover   = itemView.findViewById(R.id.iv_entry_cover);
            tvDayNumber    = itemView.findViewById(R.id.tv_day_number);
            tvMonthShort   = itemView.findViewById(R.id.tv_month_short);
            tvEntryTitle   = itemView.findViewById(R.id.tv_entry_title);
            tvEntryPreview = itemView.findViewById(R.id.tv_entry_preview);
        }

        void bind(DiaryEntry entry) {

            String coverPath = resolveCoverPath(entry);

            if (coverPath != null) {
                currentImagePath = coverPath;
                ivEntryCover.setVisibility(View.VISIBLE);
                ivEntryCover.setImageBitmap(null);

                final String pathToLoad = coverPath;
                executor.execute(() -> {
                    Bitmap bmp = decodeSampled(pathToLoad, 200, 200);
                    mainHandler.post(() -> {

                        if (pathToLoad.equals(currentImagePath)) {
                            if (bmp != null) {
                                ivEntryCover.setImageBitmap(bmp);
                            } else {
                                ivEntryCover.setVisibility(View.GONE);
                            }
                        }
                    });
                });
            } else {
                currentImagePath = null;
                ivEntryCover.setVisibility(View.GONE);
                ivEntryCover.setImageBitmap(null);
            }


            tvEntryTitle.setText(entry.getTitle());

            String content = entry.getDiaryEntry() != null ? entry.getDiaryEntry() : "";
            tvEntryPreview.setText(content.length() > 80
                    ? content.substring(0, 80) + "…"
                    : content);


            parseDateIntoBlock(entry.getDate());


            itemView.setOnClickListener(v -> listener.onEntryClick(entry));
        }


        private String resolveCoverPath(DiaryEntry entry) {
            String cover = entry.getCoverPhotoUri();
            if (isValidFile(cover)) return cover;

            List<String> uris = entry.getImageUris();
            if (uris != null) {
                for (String uri : uris) {
                    if (isValidFile(uri)) return uri;
                }
            }
            return null;
        }

        private boolean isValidFile(String path) {
            return path != null && !path.isEmpty() && new File(path).exists();
        }


        private Bitmap decodeSampled(String path, int reqW, int reqH) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, opts);

                opts.inSampleSize    = calculateInSampleSize(opts, reqW, reqH);
                opts.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(path, opts);
            } catch (Exception e) {
                return null;
            }
        }

        private int calculateInSampleSize(BitmapFactory.Options opts, int reqW, int reqH) {
            int h = opts.outHeight, w = opts.outWidth;
            int sample = 1;
            if (h > reqH || w > reqW) {
                int halfH = h / 2, halfW = w / 2;
                while ((halfH / sample) >= reqH && (halfW / sample) >= reqW) sample *= 2;
            }
            return sample;
        }

        private void parseDateIntoBlock(String date) {
            if (date == null || date.isEmpty()) {
                tvDayNumber.setText("—");
                tvMonthShort.setText("");
                return;
            }
            try {
                String[] parts = date.trim().split("\\s+");
                tvDayNumber.setText(parts.length > 0 ? parts[0] : "—");

                if (parts.length >= 2) {
                    String monthName = parts[1].toUpperCase(java.util.Locale.forLanguageTag("tr"));
                    String abbrev = monthName.length() >= 3 ? monthName.substring(0, 3) : monthName;
                    tvMonthShort.setText(abbrev);
                } else {
                    tvMonthShort.setText("");
                }
            } catch (Exception e) {
                tvDayNumber.setText("—");
                tvMonthShort.setText("");
            }
        }
    }
}