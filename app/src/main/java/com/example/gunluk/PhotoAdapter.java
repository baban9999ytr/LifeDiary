package com.example.gunluk;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    public interface OnCoverSelectedListener {
        void onCoverSelected(int position);
    }

    private final List<String> paths = new ArrayList<>();
    private int coverIndex = -1;
    private OnCoverSelectedListener coverListener;

    public void setPaths(List<String> paths) {
        this.paths.clear();
        if (paths != null) this.paths.addAll(paths);
        notifyDataSetChanged();
    }

    public List<String> getPaths() {
        return new ArrayList<>(paths);
    }

    public void setCoverIndex(int index) {
        int old = coverIndex;
        coverIndex = index;
        if (old >= 0) notifyItemChanged(old);
        if (coverIndex >= 0) notifyItemChanged(coverIndex);
    }

    public int getCoverIndex() {
        return coverIndex;
    }

    public String getCoverPath() {
        if (coverIndex >= 0 && coverIndex < paths.size()) return paths.get(coverIndex);
        return paths.isEmpty() ? null : paths.get(0);
    }

    public void setOnCoverSelectedListener(OnCoverSelectedListener listener) {
        this.coverListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String path = paths.get(position);
        boolean isCover = (position == coverIndex);
        holder.ivCoverBadge.setVisibility(isCover ? View.VISIBLE : View.GONE);
        holder.tvSetCover.setOnClickListener(v -> {
            setCoverIndex(position);
            if (coverListener != null) coverListener.onCoverSelected(position);
        });
        File f = new File(path);
        if (f.exists()) {
            holder.ivPhoto.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
        } else {
            holder.ivPhoto.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvSetCover;
        ImageView ivCoverBadge;

        PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvSetCover = itemView.findViewById(R.id.tv_set_cover);
            ivCoverBadge = itemView.findViewById(R.id.iv_cover_badge);
        }
    }
}
