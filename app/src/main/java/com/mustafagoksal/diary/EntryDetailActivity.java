package com.mustafagoksal.diary;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafagoksal.diary.database.RoomDB;
import com.mustafagoksal.diary.models.DiaryEntry;

import java.io.File;
import java.util.List;

public class EntryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ENTRY_ID = "entry_id";

    // ── Views ────────────────────────────────────────────────────
    private ImageView    ivDetailCover;
    private TextView     tvTitle, tvDetailWeather, chipSteps, chipLocation;
    private LinearLayout rowContext, sectionTasks, containerDetailTasks;
    private TextView     tvDate;
    private WebView      wvContent;
    private RecyclerView recyclerDetailPhotos;
    private Button       btnPlayVoiceDetail;
    private LinearLayout sectionLinks;
    private RecyclerView recyclerDetailLinks;
    private LinearLayout sectionHandwriting;
    private ImageView    ivDetailHandwriting;
    private LinearLayout sectionVideos;
    private VideoView    videoView;
    private RecyclerView recyclerDetailVideos;
    private Button       btnDelete;
    private Button       btnEdit;
    private Button       btnHome;


    private RoomDB       db;
    private DiaryEntry   currentEntry;
    private MediaPlayer  mediaPlayer;
    private PhotoAdapter detailPhotoAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = RoomDB.getInstance(this);
        long entryId = getIntent().getLongExtra(EXTRA_ENTRY_ID, -1L);
        boolean themeApplied = false;

        if (entryId != -1L) {
            DiaryEntry tempEntry = db.mainDAO().getEntryById(entryId);
            if (tempEntry != null && "solarpunk".equals(tempEntry.getMoodTheme())) {
                setTheme(R.style.Theme_Gunluk_Solarpunk);
                themeApplied = true;
            }
        }
        if (!themeApplied) ThemeHelper.applyTheme(this);

        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_entry_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        bindViews();
        if (entryId == -1L) {
            Toast.makeText(this, R.string.entry_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEntry(entryId);

        btnDelete.setOnClickListener(v -> confirmDelete());
        btnEdit.setOnClickListener(v -> openEditMode());
        btnHome.setOnClickListener(v -> goHome());
    }



    private void bindViews() {
        ivDetailCover        = findViewById(R.id.iv_detail_cover);
        ivDetailHandwriting  = findViewById(R.id.iv_detail_handwriting);
        sectionHandwriting   = findViewById(R.id.section_handwriting);
        tvTitle              = findViewById(R.id.tv_detail_title);
        tvDetailWeather      = findViewById(R.id.chip_weather);
        chipSteps            = findViewById(R.id.chip_steps);
        chipLocation         = findViewById(R.id.chip_location);
        rowContext           = findViewById(R.id.row_context);
        sectionTasks         = findViewById(R.id.section_tasks);
        containerDetailTasks = findViewById(R.id.container_detail_tasks);
        tvDate               = findViewById(R.id.tv_detail_date);
        wvContent            = findViewById(R.id.wv_detail_content);
        recyclerDetailPhotos = findViewById(R.id.recycler_detail_photos);
        btnPlayVoiceDetail   = findViewById(R.id.btn_play_voice_detail);
        sectionLinks         = findViewById(R.id.section_links);
        recyclerDetailLinks  = findViewById(R.id.recycler_detail_links);
        sectionVideos        = findViewById(R.id.section_videos);
        videoView            = findViewById(R.id.video_view);
        recyclerDetailVideos = findViewById(R.id.recycler_detail_videos);
        btnDelete            = findViewById(R.id.btn_delete);
        btnEdit              = findViewById(R.id.btn_edit);
        btnHome              = findViewById(R.id.btn_home);
    }


    private void loadEntry(long id) {
        currentEntry = db.mainDAO().getEntryById(id);
        if (currentEntry == null) {
            Toast.makeText(this, R.string.entry_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        if (EntryPasswordHelper.isLocked(currentEntry.getEntryPasswordHash())) {
            showPasswordDialog();
        } else {
            displayEntry();
        }
    }

    private void showPasswordDialog() {
        android.widget.EditText etPass = new android.widget.EditText(this);
        etPass.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        etPass.setHint(R.string.hint_entry_password);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_entry_locked)
                .setView(etPass)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_unlock, (d, w) -> {
                    String input = etPass.getText().toString();
                    if (EntryPasswordHelper.verify(input, currentEntry.getEntryPasswordHash())) {
                        displayEntry();
                    } else {
                        Toast.makeText(this, R.string.toast_wrong_password, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, (d, w) -> finish())
                .show();
    }

    private void displayEntry() {
        tvTitle.setText(currentEntry.getTitle());
        tvDate.setText(currentEntry.getDate());
        
        boolean anyContext = false;
        if (currentEntry.getWeather() != null && !currentEntry.getWeather().isEmpty()) {
            tvDetailWeather.setText("🌤 " + currentEntry.getWeather());
            tvDetailWeather.setVisibility(View.VISIBLE);
            anyContext = true;
        }
        if (currentEntry.getLocationInfo() != null && !currentEntry.getLocationInfo().isEmpty()) {
            chipLocation.setText("📍 " + currentEntry.getLocationInfo());
            chipLocation.setVisibility(View.VISIBLE);
            anyContext = true;
        }
        if (currentEntry.getStepCount() > 0) {
            chipSteps.setText("🚶 " + currentEntry.getStepCount() + " steps");
            chipSteps.setVisibility(View.VISIBLE);
            anyContext = true;
        }
        if (anyContext) {
            rowContext.setVisibility(View.VISIBLE);
        }

        String coverUri = currentEntry.getCoverPhotoUri();
        if (coverUri != null && new File(coverUri).exists()) {
            ivDetailCover.setImageURI(Uri.parse(coverUri));
            ivDetailCover.setVisibility(View.VISIBLE);
        }


        String hwPath = currentEntry.getHandwritingImagePath();
        if (hwPath != null && new File(hwPath).exists()) {
            ivDetailHandwriting.setImageURI(Uri.parse(hwPath));
            sectionHandwriting.setVisibility(View.VISIBLE);
        }


        String rich = currentEntry.getRichContent();
        if (rich != null && !rich.isEmpty()) {
            setupWebView(rich);
        } else {
            // Fallback: wrap plain text in minimal HTML
            String plain = currentEntry.getDiaryEntry();
            setupWebView("<p>" + (plain != null ? plain : "") + "</p>");
        }


        List<String> uris = currentEntry.getImageUris();
        if (uris != null && !uris.isEmpty()) {
            detailPhotoAdapter = new PhotoAdapter();
            recyclerDetailPhotos.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            );
            recyclerDetailPhotos.setAdapter(detailPhotoAdapter);
            detailPhotoAdapter.setPaths(uris);
            detailPhotoAdapter.setCoverIndex(-1);
            recyclerDetailPhotos.setVisibility(View.VISIBLE);
        }


        String audioPath = currentEntry.getAudioPath();
        if (audioPath != null && new File(audioPath).exists()) {
            btnPlayVoiceDetail.setVisibility(View.VISIBLE);
            btnPlayVoiceDetail.setText(R.string.btn_play);
            btnPlayVoiceDetail.setOnClickListener(v -> togglePlayVoice(audioPath));
        }


        boolean tasksLoaded = false;
        List<com.mustafagoksal.diary.models.TaskCompletion> completions = db.mainDAO().getTaskCompletionsByDate(currentEntry.getDate());
        if (completions != null && !completions.isEmpty()) {
            for (com.mustafagoksal.diary.models.TaskCompletion tc : completions) {
                if (tc.isCompleted) {
                    com.mustafagoksal.diary.models.DailyTask dt = db.mainDAO().getTaskById(tc.taskId);
                    if (dt != null && dt.username.equals(currentEntry.getAuthor())) {
                        TextView tvTask = new TextView(this);
                        tvTask.setText("✅ " + dt.taskName);
                        tvTask.setTextColor(Color.parseColor("#4A4A4A"));
                        tvTask.setPadding(0, 8, 0, 8);
                        containerDetailTasks.addView(tvTask);
                        tasksLoaded = true;
                    }
                }
            }
        }
        if (tasksLoaded) {
            sectionTasks.setVisibility(View.VISIBLE);
        }

        List<String> links = currentEntry.getLinks();
        if (links != null && !links.isEmpty()) {
            sectionLinks.setVisibility(View.VISIBLE);
            recyclerDetailLinks.setLayoutManager(new LinearLayoutManager(this));
            recyclerDetailLinks.setAdapter(new LinksReadAdapter(links));
            recyclerDetailLinks.setNestedScrollingEnabled(false);
        }


        List<String> videos = currentEntry.getVideoUris();
        if (videos != null && !videos.isEmpty()) {
            sectionVideos.setVisibility(View.VISIBLE);
            playVideo(videos.get(0));
            if (videos.size() > 1) {
                recyclerDetailVideos.setVisibility(View.VISIBLE);
                recyclerDetailVideos.setLayoutManager(
                        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                );
                recyclerDetailVideos.setAdapter(new VideoThumbAdapter(videos, this::playVideo));
            }
        }
    }


    private void setupWebView(String html) {
        wvContent.setBackgroundColor(Color.TRANSPARENT);
        wvContent.getSettings().setJavaScriptEnabled(false);
        wvContent.getSettings().setLoadWithOverviewMode(true);
        wvContent.getSettings().setUseWideViewPort(true);


        String fullHtml =
                "<html><head><style>" +
                        "  body { font-size: 16px;" +
                        "         margin: 0; padding: 0; background: transparent; }" +
                        "  blockquote { border-left: 3px solid #C0714A; margin-left: 8px;" +
                        "               padding-left: 12px; color: #7a5c50; }" +
                        "</style></head><body>" + html + "</body></html>";

        wvContent.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null);
    }



    private void togglePlayVoice(String path) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btnPlayVoiceDetail.setText(R.string.btn_play);
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
                btnPlayVoiceDetail.setText(R.string.btn_play);
            });
            mediaPlayer.start();
            btnPlayVoiceDetail.setText(R.string.btn_stop_play);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_play_failed, Toast.LENGTH_SHORT).show();
        }
    }



    private void playVideo(String uriString) {
        videoView.setVideoURI(Uri.parse(uriString));
        videoView.setMediaController(new android.widget.MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }



    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, currentEntry.getTitle()))
                .setPositiveButton(R.string.delete_confirm_yes, (d, w) -> deleteEntry())
                .setNegativeButton(R.string.delete_confirm_cancel, null)
                .show();
    }

    private void deleteEntry() {
        db.mainDAO().deleteById(currentEntry.getID());
        Toast.makeText(this, R.string.entry_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openEditMode() {
        Intent intent = new Intent(this, NewEntryActivity.class);
        intent.putExtra(NewEntryActivity.EXTRA_ENTRY_ID, currentEntry.getID());
        startActivity(intent);
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            try { mediaPlayer.stop(); mediaPlayer.release(); } catch (Exception ignored) {}
            mediaPlayer = null;
        }
        if (videoView != null) videoView.stopPlayback();
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    private static class LinksReadAdapter
            extends RecyclerView.Adapter<LinksReadAdapter.VH> {

        private final List<String> data;
        LinksReadAdapter(List<String> data) { this.data = data; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(0, 10, 0, 10);
            tv.setTextColor(Color.parseColor("#1E88E5"));
            tv.setTextSize(13);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setMaxLines(1);
            return new VH(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            h.tv.setText(data.get(pos));
            h.tv.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(pos)));
                v.getContext().startActivity(i);
            });
        }

        @Override public int getItemCount() { return data.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tv;
            VH(TextView v) { super(v); tv = v; }
        }
    }



    private static class VideoThumbAdapter
            extends RecyclerView.Adapter<VideoThumbAdapter.VH> {

        interface OnVideoClick { void play(String uri); }

        private final List<String> data;
        private final OnVideoClick listener;

        VideoThumbAdapter(List<String> data, OnVideoClick listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            Button btn = new Button(parent.getContext());
            btn.setTextSize(11);
            int size = (int) (80 * parent.getContext().getResources().getDisplayMetrics().density);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
            lp.setMarginEnd((int)(8 * parent.getContext().getResources().getDisplayMetrics().density));
            btn.setLayoutParams(lp);
            return new VH(btn);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            h.btn.setText(getString(pos));
            h.btn.setOnClickListener(v -> listener.play(data.get(pos)));
        }

        private String getString(int pos) { return "▶ " + (pos + 1); }

        @Override public int getItemCount() { return data.size(); }
        static class VH extends RecyclerView.ViewHolder {
            Button btn;
            VH(Button v) { super(v); btn = v; }
        }
    }
}