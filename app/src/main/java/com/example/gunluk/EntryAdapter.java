package com.example.gunluk;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gunluk.models.DiaryEntry;

import java.util.List;

/**
 * EntryAdapter — item_entry.xml
 *
 * RecyclerView adaptörü. Her satırda:
 *  - Gün numarası + ay kısaltması (kırmızı blok)
 *  - Başlık
 *  - İçerik önizlemesi (ilk 80 karakter)
 *
 * Tıklamalar OnEntryClickListener arayüzü üzerinden iletilir.
 */
public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    public interface OnEntryClickListener {
        void onEntryClick(DiaryEntry entry);
    }

    private final List<DiaryEntry>      entries;
    private final OnEntryClickListener  listener;

    // Türkçe ay kısaltmaları
    private static final String[] MONTHS_TR = {
        "OCA","ŞUB","MAR","NİS","MAY","HAZ",
        "TEM","AĞU","EYL","EKİ","KAS","ARA"
    };

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
        DiaryEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    // -------------------------------------------------------

    class EntryViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivEntryCover;
        private final TextView tvDayNumber;
        private final TextView tvMonthShort;
        private final TextView tvEntryTitle;
        private final TextView tvEntryPreview;

        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEntryCover   = itemView.findViewById(R.id.iv_entry_cover);
            tvDayNumber    = itemView.findViewById(R.id.tv_day_number);
            tvMonthShort   = itemView.findViewById(R.id.tv_month_short);
            tvEntryTitle   = itemView.findViewById(R.id.tv_entry_title);
            tvEntryPreview = itemView.findViewById(R.id.tv_entry_preview);
        }

        void bind(DiaryEntry entry) {
            String coverUri = entry.getCoverPhotoUri();
            if (coverUri != null && !coverUri.isEmpty() && new File(coverUri).exists()) {
                ivEntryCover.setVisibility(View.VISIBLE);
                ivEntryCover.setImageBitmap(BitmapFactory.decodeFile(coverUri));
            } else {
                List<String> uris = entry.getImageUris();
                if (uris != null && !uris.isEmpty() && new File(uris.get(0)).exists()) {
                    ivEntryCover.setVisibility(View.VISIBLE);
                    ivEntryCover.setImageBitmap(BitmapFactory.decodeFile(uris.get(0)));
                } else {
                    ivEntryCover.setVisibility(View.GONE);
                }
            }
            tvEntryTitle.setText(entry.getTitle());

            // İçerik önizlemesi — ilk 80 karakter
            String content = entry.getDiaryEntry() != null ? entry.getDiaryEntry() : "";
            tvEntryPreview.setText(content.length() > 80 ? content.substring(0, 80) + "…" : content);

            // Tarihi parse et (örn: "5 Ocak 2025, Çarşamba")
            parseDateIntoBlock(entry.getDate());

            // Tıklama
            itemView.setOnClickListener(v -> listener.onEntryClick(entry));
        }

        /**
         * Tarih string'inden gün numarasını ve ay kısaltmasını çıkar.
         * Format: "d MMMM yyyy, EEEE"  →  "5 Ocak 2025, Çarşamba"
         * Bozuk format durumunda tüm date string'ini göster.
         */
        private void parseDateIntoBlock(String date) {
            if (date == null || date.isEmpty()) {
                tvDayNumber.setText("—");
                tvMonthShort.setText("");
                return;
            }
            try {
                // "5 Ocak 2025, Çarşamba" → split by space → [5, Ocak, 2025,, Çarşamba]
                String[] parts = date.trim().split("\\s+");
                tvDayNumber.setText(parts.length > 0 ? parts[0] : "—");

                // Ay adından kısaltma üret
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
