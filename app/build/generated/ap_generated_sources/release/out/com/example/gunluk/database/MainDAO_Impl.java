package com.example.gunluk.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.gunluk.models.DiaryEntry;
import com.example.gunluk.models.Users;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MainDAO_Impl implements MainDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DiaryEntry> __insertionAdapterOfDiaryEntry;

  private final EntityInsertionAdapter<Users> __insertionAdapterOfUsers;

  private final EntityDeletionOrUpdateAdapter<DiaryEntry> __deletionAdapterOfDiaryEntry;

  private final EntityDeletionOrUpdateAdapter<DiaryEntry> __updateAdapterOfDiaryEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfUpdate;

  public MainDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDiaryEntry = new EntityInsertionAdapter<DiaryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `diaryEntry` (`ID`,`title`,`diaryEntry`,`richContent`,`date`,`pinned`,`author`,`imageUris`,`coverPhotoUri`,`audioPath`,`stepCount`,`locationCity`,`locationNeighbourhood`,`latitude`,`longitude`,`weatherDescription`,`weatherIconCode`,`links`,`videoUris`,`fontFamily`,`entryPasswordHash`,`handwritingImagePath`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DiaryEntry entity) {
        statement.bindLong(1, entity.getID());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getDiaryEntry() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDiaryEntry());
        }
        if (entity.getRichContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRichContent());
        }
        if (entity.getDate() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDate());
        }
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getAuthor() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAuthor());
        }
        final String _tmp_1 = Converters.listToString(entity.getImageUris());
        if (_tmp_1 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_1);
        }
        if (entity.getCoverPhotoUri() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCoverPhotoUri());
        }
        if (entity.getAudioPath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAudioPath());
        }
        statement.bindLong(11, entity.getStepCount());
        if (entity.getLocationCity() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getLocationCity());
        }
        if (entity.getLocationNeighbourhood() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getLocationNeighbourhood());
        }
        statement.bindDouble(14, entity.getLatitude());
        statement.bindDouble(15, entity.getLongitude());
        if (entity.getWeatherDescription() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getWeatherDescription());
        }
        if (entity.getWeatherIconCode() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getWeatherIconCode());
        }
        final String _tmp_2 = Converters.listToString(entity.getLinks());
        if (_tmp_2 == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, _tmp_2);
        }
        final String _tmp_3 = Converters.listToString(entity.getVideoUris());
        if (_tmp_3 == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, _tmp_3);
        }
        if (entity.getFontFamily() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getFontFamily());
        }
        if (entity.getEntryPasswordHash() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getEntryPasswordHash());
        }
        if (entity.getHandwritingImagePath() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getHandwritingImagePath());
        }
      }
    };
    this.__insertionAdapterOfUsers = new EntityInsertionAdapter<Users>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`userID`,`username`,`email`,`password`,`profile_photo_uri`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Users entity) {
        statement.bindLong(1, entity.getUserID());
        if (entity.getUsername() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUsername());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEmail());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPassword());
        }
        if (entity.getProfilePhotoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getProfilePhotoUri());
        }
      }
    };
    this.__deletionAdapterOfDiaryEntry = new EntityDeletionOrUpdateAdapter<DiaryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `diaryEntry` WHERE `ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DiaryEntry entity) {
        statement.bindLong(1, entity.getID());
      }
    };
    this.__updateAdapterOfDiaryEntry = new EntityDeletionOrUpdateAdapter<DiaryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `diaryEntry` SET `ID` = ?,`title` = ?,`diaryEntry` = ?,`richContent` = ?,`date` = ?,`pinned` = ?,`author` = ?,`imageUris` = ?,`coverPhotoUri` = ?,`audioPath` = ?,`stepCount` = ?,`locationCity` = ?,`locationNeighbourhood` = ?,`latitude` = ?,`longitude` = ?,`weatherDescription` = ?,`weatherIconCode` = ?,`links` = ?,`videoUris` = ?,`fontFamily` = ?,`entryPasswordHash` = ?,`handwritingImagePath` = ? WHERE `ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DiaryEntry entity) {
        statement.bindLong(1, entity.getID());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getDiaryEntry() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDiaryEntry());
        }
        if (entity.getRichContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRichContent());
        }
        if (entity.getDate() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDate());
        }
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getAuthor() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAuthor());
        }
        final String _tmp_1 = Converters.listToString(entity.getImageUris());
        if (_tmp_1 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_1);
        }
        if (entity.getCoverPhotoUri() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCoverPhotoUri());
        }
        if (entity.getAudioPath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAudioPath());
        }
        statement.bindLong(11, entity.getStepCount());
        if (entity.getLocationCity() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getLocationCity());
        }
        if (entity.getLocationNeighbourhood() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getLocationNeighbourhood());
        }
        statement.bindDouble(14, entity.getLatitude());
        statement.bindDouble(15, entity.getLongitude());
        if (entity.getWeatherDescription() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getWeatherDescription());
        }
        if (entity.getWeatherIconCode() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getWeatherIconCode());
        }
        final String _tmp_2 = Converters.listToString(entity.getLinks());
        if (_tmp_2 == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, _tmp_2);
        }
        final String _tmp_3 = Converters.listToString(entity.getVideoUris());
        if (_tmp_3 == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, _tmp_3);
        }
        if (entity.getFontFamily() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getFontFamily());
        }
        if (entity.getEntryPasswordHash() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getEntryPasswordHash());
        }
        if (entity.getHandwritingImagePath() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getHandwritingImagePath());
        }
        statement.bindLong(23, entity.getID());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM diaryEntry WHERE ID = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE diaryEntry SET title = ?, diaryEntry = ?, date = ?, pinned = ? WHERE ID = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertDiary(final DiaryEntry diaryEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDiaryEntry.insert(diaryEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertUser(final Users users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUsers.insert(users);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteDiary(final DiaryEntry diaryEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfDiaryEntry.handle(diaryEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDiary(final DiaryEntry diaryEntry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDiaryEntry.handle(diaryEntry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteById(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteById.release(_stmt);
    }
  }

  @Override
  public void update(final long id, final String title, final String content, final String date,
      final boolean pinned) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdate.acquire();
    int _argIndex = 1;
    if (title == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, title);
    }
    _argIndex = 2;
    if (content == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, content);
    }
    _argIndex = 3;
    if (date == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, date);
    }
    _argIndex = 4;
    final int _tmp = pinned ? 1 : 0;
    _stmt.bindLong(_argIndex, _tmp);
    _argIndex = 5;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdate.release(_stmt);
    }
  }

  @Override
  public List<DiaryEntry> getAllDiaryEntries() {
    final String _sql = "SELECT * FROM diaryEntry ORDER BY ID DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfID = CursorUtil.getColumnIndexOrThrow(_cursor, "ID");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDiaryEntry = CursorUtil.getColumnIndexOrThrow(_cursor, "diaryEntry");
      final int _cursorIndexOfRichContent = CursorUtil.getColumnIndexOrThrow(_cursor, "richContent");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "pinned");
      final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
      final int _cursorIndexOfImageUris = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUris");
      final int _cursorIndexOfCoverPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhotoUri");
      final int _cursorIndexOfAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioPath");
      final int _cursorIndexOfStepCount = CursorUtil.getColumnIndexOrThrow(_cursor, "stepCount");
      final int _cursorIndexOfLocationCity = CursorUtil.getColumnIndexOrThrow(_cursor, "locationCity");
      final int _cursorIndexOfLocationNeighbourhood = CursorUtil.getColumnIndexOrThrow(_cursor, "locationNeighbourhood");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
      final int _cursorIndexOfWeatherIconCode = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIconCode");
      final int _cursorIndexOfLinks = CursorUtil.getColumnIndexOrThrow(_cursor, "links");
      final int _cursorIndexOfVideoUris = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUris");
      final int _cursorIndexOfFontFamily = CursorUtil.getColumnIndexOrThrow(_cursor, "fontFamily");
      final int _cursorIndexOfEntryPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPasswordHash");
      final int _cursorIndexOfHandwritingImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "handwritingImagePath");
      final List<DiaryEntry> _result = new ArrayList<DiaryEntry>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final DiaryEntry _item;
        _item = new DiaryEntry();
        final long _tmpID;
        _tmpID = _cursor.getLong(_cursorIndexOfID);
        _item.setID(_tmpID);
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        _item.setTitle(_tmpTitle);
        final String _tmpDiaryEntry;
        if (_cursor.isNull(_cursorIndexOfDiaryEntry)) {
          _tmpDiaryEntry = null;
        } else {
          _tmpDiaryEntry = _cursor.getString(_cursorIndexOfDiaryEntry);
        }
        _item.setDiaryEntry(_tmpDiaryEntry);
        final String _tmpRichContent;
        if (_cursor.isNull(_cursorIndexOfRichContent)) {
          _tmpRichContent = null;
        } else {
          _tmpRichContent = _cursor.getString(_cursorIndexOfRichContent);
        }
        _item.setRichContent(_tmpRichContent);
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        _item.setDate(_tmpDate);
        final boolean _tmpPinned;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfPinned);
        _tmpPinned = _tmp != 0;
        _item.setPinned(_tmpPinned);
        final String _tmpAuthor;
        if (_cursor.isNull(_cursorIndexOfAuthor)) {
          _tmpAuthor = null;
        } else {
          _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
        }
        _item.setAuthor(_tmpAuthor);
        final List<String> _tmpImageUris;
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfImageUris)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfImageUris);
        }
        _tmpImageUris = Converters.fromString(_tmp_1);
        _item.setImageUris(_tmpImageUris);
        final String _tmpCoverPhotoUri;
        if (_cursor.isNull(_cursorIndexOfCoverPhotoUri)) {
          _tmpCoverPhotoUri = null;
        } else {
          _tmpCoverPhotoUri = _cursor.getString(_cursorIndexOfCoverPhotoUri);
        }
        _item.setCoverPhotoUri(_tmpCoverPhotoUri);
        final String _tmpAudioPath;
        if (_cursor.isNull(_cursorIndexOfAudioPath)) {
          _tmpAudioPath = null;
        } else {
          _tmpAudioPath = _cursor.getString(_cursorIndexOfAudioPath);
        }
        _item.setAudioPath(_tmpAudioPath);
        final int _tmpStepCount;
        _tmpStepCount = _cursor.getInt(_cursorIndexOfStepCount);
        _item.setStepCount(_tmpStepCount);
        final String _tmpLocationCity;
        if (_cursor.isNull(_cursorIndexOfLocationCity)) {
          _tmpLocationCity = null;
        } else {
          _tmpLocationCity = _cursor.getString(_cursorIndexOfLocationCity);
        }
        _item.setLocationCity(_tmpLocationCity);
        final String _tmpLocationNeighbourhood;
        if (_cursor.isNull(_cursorIndexOfLocationNeighbourhood)) {
          _tmpLocationNeighbourhood = null;
        } else {
          _tmpLocationNeighbourhood = _cursor.getString(_cursorIndexOfLocationNeighbourhood);
        }
        _item.setLocationNeighbourhood(_tmpLocationNeighbourhood);
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.setLatitude(_tmpLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        _item.setLongitude(_tmpLongitude);
        final String _tmpWeatherDescription;
        if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
          _tmpWeatherDescription = null;
        } else {
          _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
        }
        _item.setWeatherDescription(_tmpWeatherDescription);
        final String _tmpWeatherIconCode;
        if (_cursor.isNull(_cursorIndexOfWeatherIconCode)) {
          _tmpWeatherIconCode = null;
        } else {
          _tmpWeatherIconCode = _cursor.getString(_cursorIndexOfWeatherIconCode);
        }
        _item.setWeatherIconCode(_tmpWeatherIconCode);
        final List<String> _tmpLinks;
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfLinks)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfLinks);
        }
        _tmpLinks = Converters.fromString(_tmp_2);
        _item.setLinks(_tmpLinks);
        final List<String> _tmpVideoUris;
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfVideoUris)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfVideoUris);
        }
        _tmpVideoUris = Converters.fromString(_tmp_3);
        _item.setVideoUris(_tmpVideoUris);
        final String _tmpFontFamily;
        if (_cursor.isNull(_cursorIndexOfFontFamily)) {
          _tmpFontFamily = null;
        } else {
          _tmpFontFamily = _cursor.getString(_cursorIndexOfFontFamily);
        }
        _item.setFontFamily(_tmpFontFamily);
        final String _tmpEntryPasswordHash;
        if (_cursor.isNull(_cursorIndexOfEntryPasswordHash)) {
          _tmpEntryPasswordHash = null;
        } else {
          _tmpEntryPasswordHash = _cursor.getString(_cursorIndexOfEntryPasswordHash);
        }
        _item.setEntryPasswordHash(_tmpEntryPasswordHash);
        final String _tmpHandwritingImagePath;
        if (_cursor.isNull(_cursorIndexOfHandwritingImagePath)) {
          _tmpHandwritingImagePath = null;
        } else {
          _tmpHandwritingImagePath = _cursor.getString(_cursorIndexOfHandwritingImagePath);
        }
        _item.setHandwritingImagePath(_tmpHandwritingImagePath);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DiaryEntry> getEntriesByAuthor(final String username) {
    final String _sql = "SELECT * FROM diaryEntry WHERE author = ? ORDER BY ID DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfID = CursorUtil.getColumnIndexOrThrow(_cursor, "ID");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDiaryEntry = CursorUtil.getColumnIndexOrThrow(_cursor, "diaryEntry");
      final int _cursorIndexOfRichContent = CursorUtil.getColumnIndexOrThrow(_cursor, "richContent");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "pinned");
      final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
      final int _cursorIndexOfImageUris = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUris");
      final int _cursorIndexOfCoverPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhotoUri");
      final int _cursorIndexOfAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioPath");
      final int _cursorIndexOfStepCount = CursorUtil.getColumnIndexOrThrow(_cursor, "stepCount");
      final int _cursorIndexOfLocationCity = CursorUtil.getColumnIndexOrThrow(_cursor, "locationCity");
      final int _cursorIndexOfLocationNeighbourhood = CursorUtil.getColumnIndexOrThrow(_cursor, "locationNeighbourhood");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
      final int _cursorIndexOfWeatherIconCode = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIconCode");
      final int _cursorIndexOfLinks = CursorUtil.getColumnIndexOrThrow(_cursor, "links");
      final int _cursorIndexOfVideoUris = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUris");
      final int _cursorIndexOfFontFamily = CursorUtil.getColumnIndexOrThrow(_cursor, "fontFamily");
      final int _cursorIndexOfEntryPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "entryPasswordHash");
      final int _cursorIndexOfHandwritingImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "handwritingImagePath");
      final List<DiaryEntry> _result = new ArrayList<DiaryEntry>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final DiaryEntry _item;
        _item = new DiaryEntry();
        final long _tmpID;
        _tmpID = _cursor.getLong(_cursorIndexOfID);
        _item.setID(_tmpID);
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        _item.setTitle(_tmpTitle);
        final String _tmpDiaryEntry;
        if (_cursor.isNull(_cursorIndexOfDiaryEntry)) {
          _tmpDiaryEntry = null;
        } else {
          _tmpDiaryEntry = _cursor.getString(_cursorIndexOfDiaryEntry);
        }
        _item.setDiaryEntry(_tmpDiaryEntry);
        final String _tmpRichContent;
        if (_cursor.isNull(_cursorIndexOfRichContent)) {
          _tmpRichContent = null;
        } else {
          _tmpRichContent = _cursor.getString(_cursorIndexOfRichContent);
        }
        _item.setRichContent(_tmpRichContent);
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        _item.setDate(_tmpDate);
        final boolean _tmpPinned;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfPinned);
        _tmpPinned = _tmp != 0;
        _item.setPinned(_tmpPinned);
        final String _tmpAuthor;
        if (_cursor.isNull(_cursorIndexOfAuthor)) {
          _tmpAuthor = null;
        } else {
          _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
        }
        _item.setAuthor(_tmpAuthor);
        final List<String> _tmpImageUris;
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfImageUris)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfImageUris);
        }
        _tmpImageUris = Converters.fromString(_tmp_1);
        _item.setImageUris(_tmpImageUris);
        final String _tmpCoverPhotoUri;
        if (_cursor.isNull(_cursorIndexOfCoverPhotoUri)) {
          _tmpCoverPhotoUri = null;
        } else {
          _tmpCoverPhotoUri = _cursor.getString(_cursorIndexOfCoverPhotoUri);
        }
        _item.setCoverPhotoUri(_tmpCoverPhotoUri);
        final String _tmpAudioPath;
        if (_cursor.isNull(_cursorIndexOfAudioPath)) {
          _tmpAudioPath = null;
        } else {
          _tmpAudioPath = _cursor.getString(_cursorIndexOfAudioPath);
        }
        _item.setAudioPath(_tmpAudioPath);
        final int _tmpStepCount;
        _tmpStepCount = _cursor.getInt(_cursorIndexOfStepCount);
        _item.setStepCount(_tmpStepCount);
        final String _tmpLocationCity;
        if (_cursor.isNull(_cursorIndexOfLocationCity)) {
          _tmpLocationCity = null;
        } else {
          _tmpLocationCity = _cursor.getString(_cursorIndexOfLocationCity);
        }
        _item.setLocationCity(_tmpLocationCity);
        final String _tmpLocationNeighbourhood;
        if (_cursor.isNull(_cursorIndexOfLocationNeighbourhood)) {
          _tmpLocationNeighbourhood = null;
        } else {
          _tmpLocationNeighbourhood = _cursor.getString(_cursorIndexOfLocationNeighbourhood);
        }
        _item.setLocationNeighbourhood(_tmpLocationNeighbourhood);
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.setLatitude(_tmpLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        _item.setLongitude(_tmpLongitude);
        final String _tmpWeatherDescription;
        if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
          _tmpWeatherDescription = null;
        } else {
          _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
        }
        _item.setWeatherDescription(_tmpWeatherDescription);
        final String _tmpWeatherIconCode;
        if (_cursor.isNull(_cursorIndexOfWeatherIconCode)) {
          _tmpWeatherIconCode = null;
        } else {
          _tmpWeatherIconCode = _cursor.getString(_cursorIndexOfWeatherIconCode);
        }
        _item.setWeatherIconCode(_tmpWeatherIconCode);
        final List<String> _tmpLinks;
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfLinks)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfLinks);
        }
        _tmpLinks = Converters.fromString(_tmp_2);
        _item.setLinks(_tmpLinks);
        final List<String> _tmpVideoUris;
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfVideoUris)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfVideoUris);
        }
        _tmpVideoUris = Converters.fromString(_tmp_3);
        _item.setVideoUris(_tmpVideoUris);
        final String _tmpFontFamily;
        if (_cursor.isNull(_cursorIndexOfFontFamily)) {
          _tmpFontFamily = null;
        } else {
          _tmpFontFamily = _cursor.getString(_cursorIndexOfFontFamily);
        }
        _item.setFontFamily(_tmpFontFamily);
        final String _tmpEntryPasswordHash;
        if (_cursor.isNull(_cursorIndexOfEntryPasswordHash)) {
          _tmpEntryPasswordHash = null;
        } else {
          _tmpEntryPasswordHash = _cursor.getString(_cursorIndexOfEntryPasswordHash);
        }
        _item.setEntryPasswordHash(_tmpEntryPasswordHash);
        final String _tmpHandwritingImagePath;
        if (_cursor.isNull(_cursorIndexOfHandwritingImagePath)) {
          _tmpHandwritingImagePath = null;
        } else {
          _tmpHandwritingImagePath = _cursor.getString(_cursorIndexOfHandwritingImagePath);
        }
        _item.setHandwritingImagePath(_tmpHandwritingImagePath);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Users> getAllUsers() {
    final String _sql = "SELECT * FROM users";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "userID");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final int _cursorIndexOfProfilePhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_photo_uri");
      final List<Users> _result = new ArrayList<Users>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Users _item;
        _item = new Users();
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _item.setUserID(_tmpUserID);
        final String _tmpUsername;
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _tmpUsername = null;
        } else {
          _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
        }
        _item.setUsername(_tmpUsername);
        final String _tmpEmail;
        if (_cursor.isNull(_cursorIndexOfEmail)) {
          _tmpEmail = null;
        } else {
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
        }
        _item.setEmail(_tmpEmail);
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _item.setPassword(_tmpPassword);
        final String _tmpProfilePhotoUri;
        if (_cursor.isNull(_cursorIndexOfProfilePhotoUri)) {
          _tmpProfilePhotoUri = null;
        } else {
          _tmpProfilePhotoUri = _cursor.getString(_cursorIndexOfProfilePhotoUri);
        }
        _item.setProfilePhotoUri(_tmpProfilePhotoUri);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Users findByUsername(final String username) {
    final String _sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "userID");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final int _cursorIndexOfProfilePhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_photo_uri");
      final Users _result;
      if (_cursor.moveToFirst()) {
        _result = new Users();
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _result.setUserID(_tmpUserID);
        final String _tmpUsername;
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _tmpUsername = null;
        } else {
          _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
        }
        _result.setUsername(_tmpUsername);
        final String _tmpEmail;
        if (_cursor.isNull(_cursorIndexOfEmail)) {
          _tmpEmail = null;
        } else {
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
        }
        _result.setEmail(_tmpEmail);
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _result.setPassword(_tmpPassword);
        final String _tmpProfilePhotoUri;
        if (_cursor.isNull(_cursorIndexOfProfilePhotoUri)) {
          _tmpProfilePhotoUri = null;
        } else {
          _tmpProfilePhotoUri = _cursor.getString(_cursorIndexOfProfilePhotoUri);
        }
        _result.setProfilePhotoUri(_tmpProfilePhotoUri);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Users login(final String username, final String password) {
    final String _sql = "SELECT * FROM users WHERE username = ? AND password = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    _argIndex = 2;
    if (password == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, password);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(_cursor, "userID");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final int _cursorIndexOfProfilePhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_photo_uri");
      final Users _result;
      if (_cursor.moveToFirst()) {
        _result = new Users();
        final int _tmpUserID;
        _tmpUserID = _cursor.getInt(_cursorIndexOfUserID);
        _result.setUserID(_tmpUserID);
        final String _tmpUsername;
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _tmpUsername = null;
        } else {
          _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
        }
        _result.setUsername(_tmpUsername);
        final String _tmpEmail;
        if (_cursor.isNull(_cursorIndexOfEmail)) {
          _tmpEmail = null;
        } else {
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
        }
        _result.setEmail(_tmpEmail);
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _result.setPassword(_tmpPassword);
        final String _tmpProfilePhotoUri;
        if (_cursor.isNull(_cursorIndexOfProfilePhotoUri)) {
          _tmpProfilePhotoUri = null;
        } else {
          _tmpProfilePhotoUri = _cursor.getString(_cursorIndexOfProfilePhotoUri);
        }
        _result.setProfilePhotoUri(_tmpProfilePhotoUri);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int usernameExists(final String username) {
    final String _sql = "SELECT COUNT(*) FROM users WHERE username = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
