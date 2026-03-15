package com.example.gunluk;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gunluk.database.RoomDB;
import com.example.gunluk.models.DiaryEntry;
import com.example.gunluk.models.Users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;

public class NewEntryActivity extends AppCompatActivity {

    public static final String EXTRA_ENTRY_ID = "entry_id";
    private static final int MAX_PHOTOS = 10;
    private static final int MAX_VIDEOS = 10;
    private static final int REQ_RECORD_AUDIO = 100;
    private static final int REQ_ACTIVITY_RECOGNITION = 101;
    private static final int REQ_LOCATION = 102;

    private EditText etTitle;
    private RichEditor richEditor;
    private TextView tvDateFull;
    private Button btnSave;
    private Button btnHome;

    private ImageView ivCoverPhoto;
    private View coverEmptyState;

    private Button btnMoreDetails;
    private LinearLayout panelMoreDetails;
    private boolean panelExpanded = false;

    private Button btnAddPhotos;
    private RecyclerView recyclerPhotos;
    private PhotoAdapter photoAdapter;

    private Button btnAddVideo;
    private RecyclerView recyclerVideos;
    private final List<String> videoUris = new ArrayList<>();

    private Button btnRecord;
    private Button btnPlayVoice;
    private String currentAudioPath = null;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private EditText etLinkInput;
    private Button btnAddLink;
    private RecyclerView recyclerLinks;
    private final List<String> links = new ArrayList<>();
    private LinksAdapter linksAdapter;

    private CardView cardContext;
    private TextView tvSteps;
    private TextView tvLocation;
    private TextView tvWeather;

    private Button btnChooseFont;
    private String selectedFontFamily = "default";

    private Button btnHandwriting;
    private ImageView ivHandwritingThumb;
    private String currentHandwritingPath = null;

    private EditText etEntryPassword;
    private TextView tvLockIndicator;

    private RoomDB db;


    private DiaryEntry editingEntry = null;


    private String pendingCoverPhotoUri = null;
    private int    pendingStepCount     = -1;
    private double pendingLatitude      = 0.0;
    private double pendingLongitude     = 0.0;
    private String pendingLocationCity  = null;
    private String pendingLocationNeigh = null;
    private String pendingWeatherDesc   = null;
    private String pendingWeatherIcon   = null;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(
                    new ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS),
                    uris -> {
                        if (uris == null || uris.isEmpty()) return;
                        List<String> paths = copyUrisToInternalStorage(uris);
                        if (paths.isEmpty()) {
                            Toast.makeText(this, R.string.toast_photo_copy_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<String> existing = photoAdapter.getPaths();
                        int remaining = MAX_PHOTOS - existing.size();
                        for (int i = 0; i < paths.size() && remaining > 0; i++, remaining--) {
                            existing.add(paths.get(i));
                        }
                        photoAdapter.setPaths(existing);
                        if (photoAdapter.getCoverIndex() < 0 && !existing.isEmpty()) {
                            photoAdapter.setCoverIndex(0);
                        }
                        recyclerPhotos.setVisibility(View.VISIBLE);
                    }
            );

    private final ActivityResultLauncher<String> pickCoverPhoto =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) return;
                        List<Uri> single = new ArrayList<>();
                        single.add(uri);
                        List<String> paths = copyUrisToInternalStorage(single);
                        if (!paths.isEmpty()) {
                            pendingCoverPhotoUri = paths.get(0);
                            ivCoverPhoto.setImageURI(Uri.parse(pendingCoverPhotoUri));
                            ivCoverPhoto.setVisibility(View.VISIBLE);
                            coverEmptyState.setVisibility(View.GONE);
                        }
                    }
            );

    private final ActivityResultLauncher<String> pickVideo =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) return;
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException ignored) {}
                        if (videoUris.size() < MAX_VIDEOS) {
                            videoUris.add(uri.toString());
                            recyclerVideos.setVisibility(View.VISIBLE);
                            Toast.makeText(this,
                                    getString(R.string.toast_video_added, videoUris.size(), MAX_VIDEOS),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,
                                    getString(R.string.toast_max_videos, MAX_VIDEOS),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    private final ActivityResultLauncher<Intent> launchHandwriting =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            currentHandwritingPath = result.getData().getStringExtra(HandwritingActivity.EXTRA_HW_PATH);
                            if (currentHandwritingPath != null) {
                                ivHandwritingThumb.setImageURI(Uri.parse(currentHandwritingPath));
                                ivHandwritingThumb.setVisibility(View.VISIBLE);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_new_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = RoomDB.getInstance(this);

        bindViews();
        setupRichEditor();
        setupCoverPhoto();
        setupMoreDetailsPanel();
        setupPhotos();
        setupVideos();
        setupVoice();
        setupLinks();
        setupContextFetch();
        setupFontPicker();
        setupHandwriting();
        setupEntryLock();

        tvDateFull.setText(getFormattedDate());

        long editId = getIntent().getLongExtra(EXTRA_ENTRY_ID, -1L);
        if (editId != -1L) loadEntryForEditing(editId);

        btnSave.setOnClickListener(v -> saveEntry());
        btnHome.setOnClickListener(v -> goHome());
    }

    private void bindViews() {
        etTitle         = findViewById(R.id.et_entry_title);
        richEditor      = findViewById(R.id.rich_editor);
        tvDateFull      = findViewById(R.id.tv_entry_date_full);
        btnSave         = findViewById(R.id.btn_save);
        btnHome         = findViewById(R.id.btn_home);

        ivCoverPhoto    = findViewById(R.id.iv_cover_photo);
        coverEmptyState = findViewById(R.id.cover_empty_state);

        btnMoreDetails   = findViewById(R.id.btn_more_details);
        panelMoreDetails = findViewById(R.id.panel_more_details);

        btnAddPhotos   = findViewById(R.id.btn_add_photos);
        recyclerPhotos = findViewById(R.id.recycler_photos);

        btnAddVideo    = findViewById(R.id.btn_add_video);
        recyclerVideos = findViewById(R.id.recycler_videos);

        btnRecord    = findViewById(R.id.btn_record);
        btnPlayVoice = findViewById(R.id.btn_play_voice);

        etLinkInput   = findViewById(R.id.et_link_input);
        btnAddLink    = findViewById(R.id.btn_add_link);
        recyclerLinks = findViewById(R.id.recycler_links);

        cardContext = findViewById(R.id.card_context);
        tvSteps     = findViewById(R.id.tv_steps);
        tvLocation  = findViewById(R.id.tv_location);
        tvWeather   = findViewById(R.id.tv_weather);

        btnChooseFont = findViewById(R.id.btn_choose_font);

        btnHandwriting     = findViewById(R.id.btn_handwriting);
        ivHandwritingThumb = findViewById(R.id.iv_handwriting_thumb);

        etEntryPassword = findViewById(R.id.et_entry_password);
        tvLockIndicator = findViewById(R.id.tv_lock_indicator);
    }

    private void setupRichEditor() {
        richEditor.setEditorFontSize(16);
        richEditor.setEditorFontColor(getResources().getColor(R.color.ink_brown, getTheme()));
        richEditor.setPadding(4, 4, 4, 4);
        richEditor.setPlaceholder(getString(R.string.hint_content_main));
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);

        findViewById(R.id.btn_fmt_bold).setOnClickListener(v -> richEditor.setBold());
        findViewById(R.id.btn_fmt_italic).setOnClickListener(v -> richEditor.setItalic());
        findViewById(R.id.btn_fmt_underline).setOnClickListener(v -> richEditor.setUnderline());
        findViewById(R.id.btn_fmt_bullet).setOnClickListener(v -> richEditor.setBullets());
        findViewById(R.id.btn_fmt_quote).setOnClickListener(v -> richEditor.setBlockquote());

        findViewById(R.id.btn_fmt_color).setOnClickListener(v -> {
            String[] names = {"Black", "Terracotta", "Brown", "Blue", "Green", "Red"};
            String[] hex   = {"#000000", "#C0714A", "#5C4033", "#1E88E5", "#43A047", "#E53935"};
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_text_color)
                    .setItems(names, (d, i) -> richEditor.setTextColor(Color.parseColor(hex[i])))
                    .show();
        });

        findViewById(R.id.btn_fmt_bg_color).setOnClickListener(v -> {
            String[] names = {"None", "Yellow", "Green", "Pink", "Blue"};
            String[] hex   = {"#00FFFFFF", "#FFF176", "#C8E6C9", "#F8BBD0", "#BBDEFB"};
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_highlight)
                    .setItems(names, (d, i) -> richEditor.setTextBackgroundColor(Color.parseColor(hex[i])))
                    .show();
        });

        findViewById(R.id.btn_fmt_font).setOnClickListener(v -> {
            String[] fonts = {"Arial", "Georgia", "Courier New", "Times New Roman", "Verdana"};
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_font)
                    .setItems(fonts, (d, i) -> applyFontFamily(fonts[i]))
                    .show();
        });

        findViewById(R.id.btn_fmt_size).setOnClickListener(v -> {
            String[] labels = {"Small", "Normal", "Large", "Huge"};
            int[]    sizes  = {2, 3, 5, 7};
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_size)
                    .setItems(labels, (d, i) -> richEditor.setFontSize(sizes[i]))
                    .show();
        });
    }

    private void applyFontFamily(String fontName) {
        String js = "javascript:document.execCommand('fontName', false, '" + fontName + "');";
        richEditor.loadUrl(js);
    }

    private void setupCoverPhoto() {
        findViewById(R.id.btn_cover_photo).setOnClickListener(v ->
                pickCoverPhoto.launch("image/*")
        );
    }

    private void setupMoreDetailsPanel() {
        panelMoreDetails.setVisibility(View.GONE);
        btnMoreDetails.setOnClickListener(v -> {
            panelExpanded = !panelExpanded;
            panelMoreDetails.setVisibility(panelExpanded ? View.VISIBLE : View.GONE);
            btnMoreDetails.setText(panelExpanded
                    ? R.string.btn_less_details
                    : R.string.btn_more_details);
        });
    }

    private void setupPhotos() {
        photoAdapter = new PhotoAdapter();
        recyclerPhotos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerPhotos.setAdapter(photoAdapter);
        photoAdapter.setOnCoverSelectedListener(position -> {});

        btnAddPhotos.setOnClickListener(v -> {
            if (photoAdapter.getPaths().size() >= MAX_PHOTOS) {
                Toast.makeText(this,
                        getString(R.string.toast_max_photos, MAX_PHOTOS),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            pickMultipleMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
        });
    }

    private void setupVideos() {
        recyclerVideos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        btnAddVideo.setOnClickListener(v -> {
            if (videoUris.size() >= MAX_VIDEOS) {
                Toast.makeText(this,
                        getString(R.string.toast_max_videos, MAX_VIDEOS),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            pickVideo.launch("video/*");
        });
    }

    private void setupVoice() {
        btnRecord.setOnClickListener(v -> toggleRecord());
        btnPlayVoice.setOnClickListener(v -> togglePlay());
    }

    private void toggleRecord() {
        if (mediaRecorder != null) {
            try { mediaRecorder.stop(); mediaRecorder.release(); } catch (Exception ignored) {}
            mediaRecorder = null;
            btnRecord.setText(R.string.btn_record);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_RECORD_AUDIO);
            return;
        }
        File dir = new File(getFilesDir(), "entry_audio");
        if (!dir.exists() && !dir.mkdirs()) {
            Toast.makeText(this, R.string.toast_record_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(dir, "voice_" + System.currentTimeMillis() + ".m4a");
        currentAudioPath = file.getAbsolutePath();
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            btnRecord.setText(R.string.btn_stop_record);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_record_failed, Toast.LENGTH_SHORT).show();
            currentAudioPath = null;
        }
    }

    private void togglePlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btnPlayVoice.setText(R.string.btn_play);
            return;
        }
        if (currentAudioPath == null || !new File(currentAudioPath).exists()) {
            Toast.makeText(this, R.string.toast_no_audio, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(currentAudioPath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
                btnPlayVoice.setText(R.string.btn_play);
            });
            mediaPlayer.start();
            btnPlayVoice.setText(R.string.btn_stop_play);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_play_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupLinks() {
        linksAdapter = new LinksAdapter(links);
        recyclerLinks.setLayoutManager(new LinearLayoutManager(this));
        recyclerLinks.setAdapter(linksAdapter);
        recyclerLinks.setNestedScrollingEnabled(false);

        btnAddLink.setOnClickListener(v -> {
            String raw = etLinkInput.getText().toString().trim();
            if (TextUtils.isEmpty(raw)) return;
            if (links.size() >= 100) {
                Toast.makeText(this, R.string.toast_max_links, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!raw.startsWith("http://") && !raw.startsWith("https://")) raw = "https://" + raw;
            links.add(raw);
            linksAdapter.notifyItemInserted(links.size() - 1);
            recyclerLinks.setVisibility(View.VISIBLE);
            etLinkInput.setText("");
        });
    }

    private void setupContextFetch() {
        findViewById(R.id.btn_fetch_context).setOnClickListener(v -> fetchContext());
    }

    private void fetchContext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        REQ_ACTIVITY_RECOGNITION
                );
            } else {
                readSteps();
            }
        } else {
            readSteps();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION
            );
        } else {
            fetchLocationAndWeather();
        }
    }

    private void readSteps() {
        new StepCountHelper(this, new StepCountHelper.StepCallback() {
            @Override
            public void onSteps(int steps) {
                pendingStepCount = steps;
                tvSteps.setText(getString(R.string.label_steps_count, steps));
                tvSteps.setVisibility(View.VISIBLE);
                cardContext.setVisibility(View.VISIBLE);
            }
            @Override
            public void onUnavailable() {
                tvSteps.setText(R.string.label_steps_unavailable);
                cardContext.setVisibility(View.VISIBLE);
            }
        }).readOnce();
    }

    private void fetchLocationAndWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        com.google.android.gms.location.FusedLocationProviderClient fusedClient =
                com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);

        //noinspection MissingPermission
        fusedClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                tvLocation.setText(R.string.label_location_unavailable);
                cardContext.setVisibility(View.VISIBLE);
                return;
            }

            pendingLatitude  = location.getLatitude();
            pendingLongitude = location.getLongitude();

            new Thread(() -> {
                try {
                    android.location.Geocoder geocoder =
                            new android.location.Geocoder(this, Locale.getDefault());
                    List<android.location.Address> addresses =
                            geocoder.getFromLocation(pendingLatitude, pendingLongitude, 1);

                    String city = null;
                    String neighbourhood = null;
                    if (addresses != null && !addresses.isEmpty()) {
                        android.location.Address addr = addresses.get(0);
                        city = addr.getLocality();
                        if (city == null) city = addr.getAdminArea();
                        neighbourhood = addr.getSubLocality();
                    }

                    final String finalCity  = city;
                    final String finalNeigh = neighbourhood;

                    runOnUiThread(() -> {
                        pendingLocationCity  = finalCity;
                        pendingLocationNeigh = finalNeigh;

                        String loc = (finalNeigh != null ? finalNeigh + ", " : "")
                                + (finalCity != null ? finalCity : "");
                        tvLocation.setText(loc.isEmpty()
                                ? getString(R.string.label_location_found)
                                : loc);
                        cardContext.setVisibility(View.VISIBLE);

                        WeatherHelper.fetch(pendingLatitude, pendingLongitude,
                                new WeatherHelper.WeatherCallback() {
                                    @Override
                                    public void onWeather(String description, String iconCode) {
                                        pendingWeatherDesc = description;
                                        pendingWeatherIcon = iconCode;
                                        tvWeather.setText(description);
                                        cardContext.setVisibility(View.VISIBLE);
                                    }
                                    @Override
                                    public void onFailed(String reason) {
                                        tvWeather.setText(R.string.label_weather_unavailable);
                                    }
                                });
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        tvLocation.setText(R.string.label_location_unavailable);
                        cardContext.setVisibility(View.VISIBLE);
                    });
                }
            }).start();

        }).addOnFailureListener(e -> {
            tvLocation.setText(R.string.label_location_unavailable);
            cardContext.setVisibility(View.VISIBLE);
        });
    }

    private void setupFontPicker() {
        btnChooseFont.setOnClickListener(v -> {
            String[] labels = FontHelper.getFontLabels();
            String[] keys   = FontHelper.getFontKeys();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_entry_font)
                    .setItems(labels, (d, i) -> {
                        selectedFontFamily = keys[i];
                        btnChooseFont.setText(getString(R.string.btn_font_chosen, labels[i]));
                        applyFontFamily(labels[i]);
                    })
                    .show();
        });
    }

    private void setupHandwriting() {
        btnHandwriting.setOnClickListener(v -> {
            Intent intent = new Intent(this, HandwritingActivity.class);
            launchHandwriting.launch(intent);
        });
    }

    private void setupEntryLock() {
        etEntryPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                tvLockIndicator.setText(s.length() > 0 ? R.string.icon_locked : R.string.icon_unlocked);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadEntryForEditing(long id) {
        for (DiaryEntry e : db.mainDAO().getAllDiaryEntries()) {
            if (e.getID() == id) { editingEntry = e; break; }
        }
        if (editingEntry == null) return;

        etTitle.setText(editingEntry.getTitle());

        String rich = editingEntry.getRichContent();
        if (rich != null && !rich.isEmpty()) {
            richEditor.setHtml(rich);
        } else {
            richEditor.setHtml(editingEntry.getDiaryEntry());
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.edit_entry_title);

        List<String> uris = editingEntry.getImageUris();
        if (uris != null && !uris.isEmpty()) {
            photoAdapter.setPaths(uris);
            recyclerPhotos.setVisibility(View.VISIBLE);
            String cover = editingEntry.getCoverPhotoUri();
            int idx = cover != null ? uris.indexOf(cover) : 0;
            photoAdapter.setCoverIndex(idx >= 0 ? idx : 0);
        }

        String coverUri = editingEntry.getCoverPhotoUri();
        if (coverUri != null) {
            pendingCoverPhotoUri = coverUri;
            ivCoverPhoto.setImageURI(Uri.parse(coverUri));
            ivCoverPhoto.setVisibility(View.VISIBLE);
            coverEmptyState.setVisibility(View.GONE);
        }

        currentAudioPath = editingEntry.getAudioPath();

        List<String> savedVideos = editingEntry.getVideoUris();
        if (savedVideos != null && !savedVideos.isEmpty()) {
            videoUris.addAll(savedVideos);
            recyclerVideos.setVisibility(View.VISIBLE);
        }

        List<String> savedLinks = editingEntry.getLinks();
        if (savedLinks != null && !savedLinks.isEmpty()) {
            links.addAll(savedLinks);
            linksAdapter.notifyDataSetChanged();
            recyclerLinks.setVisibility(View.VISIBLE);
        }

        selectedFontFamily = editingEntry.getFontFamily();
        if (selectedFontFamily != null && !selectedFontFamily.equals("default")) {
            btnChooseFont.setText(getString(R.string.btn_font_chosen, selectedFontFamily));
        }

        String hash = editingEntry.getEntryPasswordHash();
        if (hash != null && !hash.isEmpty()) {
            tvLockIndicator.setText(R.string.icon_locked);
        }

        // Load existing context data into pending fields
        pendingStepCount     = editingEntry.getStepCount();
        pendingLatitude      = editingEntry.getLatitude();
        pendingLongitude     = editingEntry.getLongitude();
        pendingLocationCity  = editingEntry.getLocationCity();
        pendingLocationNeigh = editingEntry.getLocationNeighbourhood();
        pendingWeatherDesc   = editingEntry.getWeatherDescription();
        pendingWeatherIcon   = editingEntry.getWeatherIconCode();

        if (pendingStepCount >= 0 || pendingLocationCity != null || pendingWeatherDesc != null) {
            cardContext.setVisibility(View.VISIBLE);
            if (pendingStepCount >= 0)
                tvSteps.setText(getString(R.string.label_steps_count, pendingStepCount));
            String loc = (pendingLocationNeigh != null ? pendingLocationNeigh + ", " : "")
                    + (pendingLocationCity != null ? pendingLocationCity : "");
            if (!loc.isEmpty()) tvLocation.setText(loc);
            if (pendingWeatherDesc != null) tvWeather.setText(pendingWeatherDesc);
        }

        currentHandwritingPath = editingEntry.getHandwritingImagePath();
        if (currentHandwritingPath != null && new File(currentHandwritingPath).exists()) {
            ivHandwritingThumb.setImageURI(Uri.parse(currentHandwritingPath));
            ivHandwritingThumb.setVisibility(View.VISIBLE);
        }
    }

    private void saveEntry() {
        String title = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError(getString(R.string.error_title_empty));
            etTitle.requestFocus();
            return;
        }

        richEditor.evaluateJavascript("RE.getHtml()", rawHtml -> {

            String html = rawHtml;
            if (html == null) html = "";

            if (html.startsWith("\"") && html.endsWith("\"")) {
                html = html.substring(1, html.length() - 1);
            }

            html = html
                    .replace("\\\"", "\"")
                    .replace("\\'",  "'")
                    .replace("\\n",  "\n")
                    .replace("\\r",  "\r")
                    .replace("\\t",  "\t")
                    .replace("\\\\", "\\");

            html = html
                    .replace("\\u003c", "<").replace("\\u003C", "<")
                    .replace("\\u003e", ">").replace("\\u003E", ">")
                    .replace("\\u0026", "&")
                    .replace("\\u0022", "\"").replace("\\u0027", "'");

            String stripped = html
                    .replaceAll("<br\\s*/?>", " ")
                    .replaceAll("<[^>]+>", "")
                    .replace("&nbsp;", " ")
                    .replace("&amp;",  "&")
                    .replace("&lt;",   "<")
                    .replace("&gt;",   ">")
                    .replace("&quot;", "\"")
                    .trim();

            boolean hasText  = !TextUtils.isEmpty(stripped);
            boolean hasMedia = html.contains("<img") || html.contains("<video");

            if (!hasText && !hasMedia) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.error_content_empty,
                                Toast.LENGTH_SHORT).show()
                );
                return;
            }

            final String plainText = stripped;
            final String finalHtml = html;

            runOnUiThread(() -> persistEntry(title, finalHtml, plainText));
        });
    }

    private void persistEntry(String title, String html, String plainText) {
        Users user = CurrentUser.getUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String date          = getFormattedDate();
        List<String> imgUris = photoAdapter.getPaths();


        String coverUri = photoAdapter.getCoverPath();
        if (coverUri == null || coverUri.isEmpty()) {
            coverUri = pendingCoverPhotoUri;
        }

        String passwordInput = etEntryPassword.getText().toString();
        String passwordHash;
        if (!TextUtils.isEmpty(passwordInput)) {
            passwordHash = EntryPasswordHelper.hash(passwordInput);
        } else if (editingEntry != null) {
            passwordHash = editingEntry.getEntryPasswordHash();
        } else {
            passwordHash = null;
        }

        if (editingEntry == null) {
            DiaryEntry entry = new DiaryEntry();
            entry.setTitle(title);
            entry.setDiaryEntry(plainText);
            entry.setRichContent(html);
            entry.setDate(date);
            entry.setAuthor(user.getUsername());
            entry.setPinned(false);
            entry.setImageUris(imgUris);
            entry.setCoverPhotoUri(coverUri);
            entry.setAudioPath(currentAudioPath);
            entry.setVideoUris(new ArrayList<>(videoUris));
            entry.setLinks(new ArrayList<>(links));
            entry.setFontFamily(selectedFontFamily);
            entry.setEntryPasswordHash(passwordHash);
            entry.setHandwritingImagePath(currentHandwritingPath);
            entry.setStepCount(pendingStepCount);
            entry.setLatitude(pendingLatitude);
            entry.setLongitude(pendingLongitude);
            entry.setLocationCity(pendingLocationCity);
            entry.setLocationNeighbourhood(pendingLocationNeigh);
            entry.setWeatherDescription(pendingWeatherDesc);
            entry.setWeatherIconCode(pendingWeatherIcon);
            db.mainDAO().insertDiary(entry);
            // Gamification hook
            GamificationManager gm = new GamificationManager(this, user.getUsername());
            gm.onEntryAddedWithStats(plainText);
            Toast.makeText(this, R.string.toast_entry_saved, Toast.LENGTH_SHORT).show();
        } else {
            editingEntry.setTitle(title);
            editingEntry.setDiaryEntry(plainText);
            editingEntry.setRichContent(html);
            editingEntry.setDate(date);
            editingEntry.setImageUris(imgUris);
            editingEntry.setCoverPhotoUri(coverUri);
            editingEntry.setAudioPath(currentAudioPath);
            editingEntry.setVideoUris(new ArrayList<>(videoUris));
            editingEntry.setLinks(new ArrayList<>(links));
            editingEntry.setFontFamily(selectedFontFamily);
            editingEntry.setEntryPasswordHash(passwordHash);
            editingEntry.setHandwritingImagePath(currentHandwritingPath);
            editingEntry.setStepCount(pendingStepCount);
            editingEntry.setLatitude(pendingLatitude);
            editingEntry.setLongitude(pendingLongitude);
            editingEntry.setLocationCity(pendingLocationCity);
            editingEntry.setLocationNeighbourhood(pendingLocationNeigh);
            editingEntry.setWeatherDescription(pendingWeatherDesc);
            editingEntry.setWeatherIconCode(pendingWeatherIcon);
            db.mainDAO().updateDiary(editingEntry);
            Toast.makeText(this, R.string.toast_entry_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        boolean granted = results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED;
        if (req == REQ_RECORD_AUDIO && granted)         toggleRecord();
        if (req == REQ_ACTIVITY_RECOGNITION && granted) readSteps();
        if (req == REQ_LOCATION && granted)             fetchLocationAndWeather();
    }

    private List<String> copyUrisToInternalStorage(List<Uri> uris) {
        List<String> paths = new ArrayList<>();
        File dir = new File(getFilesDir(), "entry_photos");
        if (!dir.exists() && !dir.mkdirs()) return paths;
        ContentResolver cr = getContentResolver();
        for (Uri uri : uris) {
            try {
                String name = "img_" + System.currentTimeMillis() + "_" + paths.size() + ".jpg";
                File out = new File(dir, name);
                try (InputStream in = cr.openInputStream(uri);
                     FileOutputStream outStream = new FileOutputStream(out)) {
                    if (in == null) continue;
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) outStream.write(buf, 0, len);
                }
                paths.add(out.getAbsolutePath());
            } catch (Exception ignored) {}
        }
        return paths;
    }

    private String getFormattedDate() {
        return new SimpleDateFormat("d MMMM yyyy, EEEE", Locale.getDefault()).format(new Date());
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            try { mediaRecorder.stop(); mediaRecorder.release(); } catch (Exception ignored) {}
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            try { mediaPlayer.stop(); mediaPlayer.release(); } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    private static class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.VH> {
        private final List<String> data;
        LinksAdapter(List<String> data) { this.data = data; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(0, 12, 0, 12);
            tv.setTextColor(Color.parseColor("#1E88E5"));
            tv.setTextSize(13);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setMaxLines(1);
            return new VH(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            h.tv.setText(data.get(pos));
            h.tv.setOnLongClickListener(v -> {
                int p = h.getAdapterPosition();
                if (p != RecyclerView.NO_ID) {
                    data.remove(p);
                    notifyItemRemoved(p);
                }
                return true;
            });
            h.tv.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(h.getAdapterPosition())));
                v.getContext().startActivity(i);
            });
        }

        @Override public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tv;
            VH(TextView v) { super(v); tv = v; }
        }
    }
}