package com.mustafagoksal.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.mustafagoksal.diary.database.RoomDB;
import com.mustafagoksal.diary.models.UserStats;

import java.util.Locale;

public class ProfileSettingsActivity extends AppCompatActivity {


    private Button btnThemeDefault, btnThemeSolarpunk, btnThemeForest, btnThemeNeon, btnThemeClay;
    private Button btnCurrEur, btnCurrUsd, btnCurrTry, btnCurrCny;
    private android.widget.EditText etHomeCity;
    private androidx.appcompat.widget.SwitchCompat switchGamification, switchStreaks;
    
    private androidx.recyclerview.widget.RecyclerView recyclerDailyTasks;
    private android.widget.EditText etNewTask;
    private Button btnAddTask;
    private TasksAdapter tasksAdapter;

    private UserStats stats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);

        if (!CurrentUser.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_profile_settings);


        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_media_previous);
        toolbar.setNavigationOnClickListener(v -> finish());


        btnThemeDefault           = findViewById(R.id.btn_theme_default);
        btnThemeSolarpunk         = findViewById(R.id.btn_theme_solarpunk);
        btnThemeForest            = findViewById(R.id.btn_theme_forest);
        btnThemeNeon              = findViewById(R.id.btn_theme_neon);
        btnThemeClay              = findViewById(R.id.btn_theme_clay);
        btnCurrEur                = findViewById(R.id.btn_curr_eur);
        btnCurrUsd                = findViewById(R.id.btn_curr_usd);
        btnCurrTry                = findViewById(R.id.btn_curr_try);
        btnCurrCny                = findViewById(R.id.btn_curr_cny);
        etHomeCity                = findViewById(R.id.et_home_city);
        recyclerDailyTasks        = findViewById(R.id.recycler_daily_tasks);
        etNewTask                 = findViewById(R.id.et_new_task);
        btnAddTask                = findViewById(R.id.btn_add_task);
        switchGamification        = findViewById(R.id.switch_gamification);
        switchStreaks             = findViewById(R.id.switch_streaks);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        etHomeCity.setText(prefs.getString("home_city", ""));

        tasksAdapter = new TasksAdapter();
        recyclerDailyTasks.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerDailyTasks.setAdapter(tasksAdapter);

        loadTasks();

        btnAddTask.setOnClickListener(v -> {
            String taskName = etNewTask.getText().toString().trim();
            if (!taskName.isEmpty()) {
                com.mustafagoksal.diary.models.DailyTask dt = new com.mustafagoksal.diary.models.DailyTask();
                dt.taskName = taskName;
                dt.username = CurrentUser.getUser().getUsername();
                dt.isActive = true;
                RoomDB.getInstance(this).mainDAO().insertTask(dt);
                etNewTask.setText("");
                loadTasks();
            }
        });

        GamificationManager gm = new GamificationManager(this, CurrentUser.getUser().getUsername());
        stats = gm.getOrCreateStats();

        if (stats != null) {
            switchGamification.setChecked(stats.gamificationEnabled);
            switchStreaks.setChecked(stats.streaksEnabled);
        }

        switchGamification.setOnCheckedChangeListener((btn, isChecked) -> {
            if (stats != null) {
                stats.gamificationEnabled = isChecked;
                saveStats();
            }
        });

        switchStreaks.setOnCheckedChangeListener((btn, isChecked) -> {
            if (stats != null) {
                stats.streaksEnabled = isChecked;
                saveStats();
            }
        });

        highlightActiveTheme();
        View.OnClickListener themeClick = v -> {
            String key;
            int id = v.getId();
            if (id == R.id.btn_theme_solarpunk) key = "solarpunk";
            else if (id == R.id.btn_theme_forest) key = "deep_forest";
            else if (id == R.id.btn_theme_neon) key = "neon_dawn";
            else if (id == R.id.btn_theme_clay) key = "arid_clay";
            else key = "default";
            getSharedPreferences("settings", MODE_PRIVATE).edit().putString("global_theme", key).apply();
            highlightActiveTheme();
            recreate();
        };
        btnThemeDefault.setOnClickListener(themeClick);
        btnThemeSolarpunk.setOnClickListener(themeClick);
        btnThemeForest.setOnClickListener(themeClick);
        btnThemeNeon.setOnClickListener(themeClick);
        btnThemeClay.setOnClickListener(themeClick);


        highlightActiveCurrency();
        View.OnClickListener currClick = v -> {
            String sym;
            int cid = v.getId();
            if (cid == R.id.btn_curr_usd) sym = "$";
            else if (cid == R.id.btn_curr_try) sym = "₺";
            else if (cid == R.id.btn_curr_cny) sym = "¥";
            else sym = "€";
            CurrencyHelper.setSymbol(this, sym);
            highlightActiveCurrency();
        };
        btnCurrEur.setOnClickListener(currClick);
        btnCurrUsd.setOnClickListener(currClick);
        btnCurrTry.setOnClickListener(currClick);
        btnCurrCny.setOnClickListener(currClick);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences("settings", MODE_PRIVATE).edit()
                .putString("home_city", etHomeCity.getText().toString().trim())
                .apply();
    }

    private void saveStats() {
        RoomDB.getInstance(this).mainDAO().insertOrUpdateStats(stats);
    }

    private void highlightActiveTheme() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String current = prefs.getString("global_theme", "default");

        int activeColor   = ContextCompat.getColor(this, R.color.accent_terracotta);
        int inactiveColor = ContextCompat.getColor(this, R.color.card_bg);
        int activeText    = ContextCompat.getColor(this, R.color.warm_white);

        Button[] btns = { btnThemeDefault, btnThemeSolarpunk, btnThemeForest, btnThemeNeon, btnThemeClay };
        String[] keys = { "default", "solarpunk", "deep_forest", "neon_dawn", "arid_clay" };
        int[] inactiveTexts = {
            ContextCompat.getColor(this, R.color.ink_black),
            ContextCompat.getColor(this, R.color.sp_primary),
            ContextCompat.getColor(this, R.color.df_primary),
            ContextCompat.getColor(this, R.color.nd_primary),
            ContextCompat.getColor(this, R.color.ac_primary)
        };
        for (int i = 0; i < btns.length; i++) {
            boolean active = keys[i].equals(current);
            btns[i].setBackgroundColor(active ? activeColor : inactiveColor);
            btns[i].setTextColor(active ? activeText : inactiveTexts[i]);
        }
    }

    private void highlightActiveCurrency() {
        String current = CurrencyHelper.getSymbol(this);
        int activeColor   = ContextCompat.getColor(this, R.color.accent_terracotta);
        int inactiveColor = ContextCompat.getColor(this, R.color.card_bg);
        int activeText    = ContextCompat.getColor(this, R.color.warm_white);
        int inactiveText  = ContextCompat.getColor(this, R.color.ink_black);

        Button[] btns = { btnCurrEur, btnCurrUsd, btnCurrTry, btnCurrCny };
        String[] syms = { "€", "$", "₺", "¥" };
        for (int i = 0; i < btns.length; i++) {
            boolean active = syms[i].equals(current);
            btns[i].setBackgroundColor(active ? activeColor : inactiveColor);
            btns[i].setTextColor(active ? activeText : inactiveText);
        }
    }

    private void loadTasks() {
        java.util.List<com.mustafagoksal.diary.models.DailyTask> list = RoomDB.getInstance(this).mainDAO().getTasksForUser(CurrentUser.getUser().getUsername());
        tasksAdapter.setTasks(list);
    }

    private class TasksAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<TasksAdapter.VH> {
        private java.util.List<com.mustafagoksal.diary.models.DailyTask> data = new java.util.ArrayList<>();

        void setTasks(java.util.List<com.mustafagoksal.diary.models.DailyTask> tasks) {
            data = tasks;
            notifyDataSetChanged();
        }

        @androidx.annotation.NonNull
        @Override
        public VH onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_task_setting, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull VH holder, int position) {
            com.mustafagoksal.diary.models.DailyTask dt = data.get(position);
            holder.tvName.setText(dt.taskName);
            holder.btnDelete.setOnClickListener(v -> {
                RoomDB.getInstance(ProfileSettingsActivity.this).mainDAO().deleteTask(dt);
                loadTasks();
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            android.widget.TextView tvName;
            android.widget.ImageButton btnDelete;
            VH(android.view.View iv) {
                super(iv);
                tvName = iv.findViewById(R.id.tv_task_name);
                btnDelete = iv.findViewById(R.id.btn_delete_task);
            }
        }
    }
}