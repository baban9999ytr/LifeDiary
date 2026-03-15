package com.mustafagoksal.diary.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mustafagoksal.diary.models.DiaryEntry;
import com.mustafagoksal.diary.models.Users;

import java.util.List;

@Dao
public interface MainDAO {



    @Insert(onConflict = REPLACE)
    void insertDiary(DiaryEntry diaryEntry);

    @Query("SELECT * FROM diaryEntry ORDER BY ID DESC")
    List<DiaryEntry> getAllDiaryEntries();

    @Query("SELECT * FROM diaryEntry WHERE author = :username ORDER BY ID DESC")
    List<DiaryEntry> getEntriesByAuthor(String username);

    @Delete
    void deleteDiary(DiaryEntry diaryEntry);

    @Query("DELETE FROM diaryEntry WHERE ID = :id")
    void deleteById(long id);

    @Insert(onConflict = REPLACE)
    void insertOrUpdateStats(RoomDB.UserStats stats);

    @Query("SELECT * FROM user_stats WHERE username = :username LIMIT 1")
    RoomDB.UserStats getStatsByUsername(String username);

    @Query("SELECT DISTINCT date FROM diaryEntry WHERE author = :username AND date LIKE '%' || :year || '%'")
    List<String> getDistinctDaysForYear(String username, String year);

    @Query("SELECT DISTINCT locationCity FROM diaryEntry WHERE author = :username AND locationCity IS NOT NULL")
    List<String> getDistinctLocations(String username);
    @Query("UPDATE diaryEntry SET title = :title, diaryEntry = :content, date = :date, pinned = :pinned WHERE ID = :id")
    void update(long id, String title, String content, String date, boolean pinned);

    @Update
    void updateDiary(DiaryEntry diaryEntry);


    @Insert(onConflict = REPLACE)
    void insertUser(Users users);

    @Query("SELECT * FROM users")
    List<Users> getAllUsers();

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    Users findByUsername(String username);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    Users login(String username, String password);

    @Query("SELECT * FROM users WHERE (username = :identifier OR email = :identifier) AND password = :password LIMIT 1")
    Users loginByUsernameOrEmail(String identifier, String password);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int usernameExists(String username);
}
