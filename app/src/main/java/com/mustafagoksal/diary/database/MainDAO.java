package com.mustafagoksal.diary.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mustafagoksal.diary.models.DiaryEntry;
import com.mustafagoksal.diary.models.Users;
import com.mustafagoksal.diary.models.UserStats;

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
    void insertOrUpdateStats(UserStats stats);

    @Query("SELECT * FROM user_stats WHERE username = :username LIMIT 1")
    UserStats getStatsByUsername(String username);

    @Query("SELECT DISTINCT date FROM diaryEntry WHERE author = :username AND date LIKE '%' || :year || '%'")
    List<String> getDistinctDaysForYear(String username, String year);

    @Query("SELECT * FROM diaryEntry WHERE ID = :id LIMIT 1")
    DiaryEntry getEntryById(long id);

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

    // --- Wallet ---
    @Insert(onConflict = REPLACE)
    void insertWalletTransaction(com.mustafagoksal.diary.models.WalletTransaction transaction);

    @Insert(onConflict = REPLACE)
    void insertTransaction(com.mustafagoksal.diary.models.WalletTransaction transaction);

    @Query("SELECT * FROM walletTransaction ORDER BY id DESC")
    List<com.mustafagoksal.diary.models.WalletTransaction> getAllWalletTransactions();

    @Query("SELECT * FROM walletTransaction WHERE username = :username ORDER BY id DESC")
    List<com.mustafagoksal.diary.models.WalletTransaction> getTransactionsForUser(String username);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM walletTransaction")
    double getWalletBalance();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM walletTransaction WHERE username = :username")
    double getWalletBalanceForUser(String username);

    @Delete
    void deleteTransaction(com.mustafagoksal.diary.models.WalletTransaction transaction);

    @Insert(onConflict = REPLACE)
    void insertTask(com.mustafagoksal.diary.models.DailyTask task);

    @Update
    void updateTask(com.mustafagoksal.diary.models.DailyTask task);

    @Delete
    void deleteTask(com.mustafagoksal.diary.models.DailyTask task);

    @Query("SELECT * FROM dailyTask WHERE isActive = 1")
    List<com.mustafagoksal.diary.models.DailyTask> getActiveTasks();

    @Query("SELECT * FROM dailyTask")
    List<com.mustafagoksal.diary.models.DailyTask> getAllTasks();

    @Query("SELECT * FROM dailyTask WHERE username = :username")
    List<com.mustafagoksal.diary.models.DailyTask> getTasksForUser(String username);

    @Query("SELECT * FROM dailyTask WHERE id = :taskId LIMIT 1")
    com.mustafagoksal.diary.models.DailyTask getTaskById(long taskId);

    @Insert(onConflict = REPLACE)
    void insertTaskCompletion(com.mustafagoksal.diary.models.TaskCompletion completion);

    @Query("SELECT * FROM taskCompletion WHERE date = :date")
    List<com.mustafagoksal.diary.models.TaskCompletion> getTaskCompletionsByDate(String date);

    @Query("SELECT * FROM taskCompletion WHERE taskId = :taskId AND date = :date LIMIT 1")
    com.mustafagoksal.diary.models.TaskCompletion getTaskCompletion(long taskId, String date);
}
