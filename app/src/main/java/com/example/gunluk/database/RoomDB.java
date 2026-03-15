package com.example.gunluk.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gunluk.models.DiaryEntry;
import com.example.gunluk.models.Users;

@Database(entities = {DiaryEntry.class, Users.class}, version = 4, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class RoomDB extends RoomDatabase {

    private static RoomDB instance;
    private static final String DATABASE_NAME = "Gunluk";

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN imageUris TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN coverPhotoUri TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN audioPath TEXT");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {

            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN richContent TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN locationCity TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN locationNeighbourhood TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN weatherDescription TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN weatherIconCode TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN links TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN videoUris TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN fontFamily TEXT");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN entryPasswordHash TEXT");


            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN stepCount INTEGER NOT NULL DEFAULT -1");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN latitude REAL NOT NULL DEFAULT 0.0");
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN longitude REAL NOT NULL DEFAULT 0.0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE diaryEntry ADD COLUMN handwritingImagePath TEXT");
        }
    };

    public abstract MainDAO mainDAO();

    public static synchronized RoomDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RoomDB.class,
                            DATABASE_NAME
                    )
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build();
        }
        return instance;
    }
}