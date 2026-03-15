package com.mustafagoksal.diary.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "diaryEntry")
public class DiaryEntry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    long ID;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "diaryEntry")
    String diaryEntry;

    @ColumnInfo(name = "richContent")
    String richContent;

    @ColumnInfo(name = "date")
    String date;

    @ColumnInfo(name = "pinned")
    boolean pinned;

    @ColumnInfo(name = "author")
    String author;

    @ColumnInfo(name = "imageUris")
    List<String> imageUris;

    @ColumnInfo(name = "coverPhotoUri")
    String coverPhotoUri;

    @ColumnInfo(name = "audioPath")
    String audioPath;

    @ColumnInfo(name = "stepCount")
    int stepCount;

    @ColumnInfo(name = "locationCity")
    String locationCity;

    @ColumnInfo(name = "locationNeighbourhood")
    String locationNeighbourhood;

    @ColumnInfo(name = "latitude")
    double latitude;

    @ColumnInfo(name = "longitude")
    double longitude;

    @ColumnInfo(name = "weatherDescription")
    String weatherDescription;

    @ColumnInfo(name = "weatherIconCode")
    String weatherIconCode;

    @ColumnInfo(name = "links")
    List<String> links;

    @ColumnInfo(name = "videoUris")
    List<String> videoUris;

    @ColumnInfo(name = "fontFamily")
    String fontFamily;

    @ColumnInfo(name = "entryPasswordHash")
    String entryPasswordHash;

    @ColumnInfo(name = "handwritingImagePath")
    String handwritingImagePath;



    public long getID() { return ID; }
    public void setID(long ID) { this.ID = ID; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getDiaryEntry() { return diaryEntry != null ? diaryEntry : ""; }
    public void setDiaryEntry(String diaryEntry) { this.diaryEntry = diaryEntry; }

    public String getRichContent() { return richContent != null ? richContent : ""; }
    public void setRichContent(String richContent) { this.richContent = richContent; }

    public String getDate() { return date != null ? date : ""; }
    public void setDate(String date) { this.date = date; }

    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }

    public String getAuthor() { return author != null ? author : ""; }
    public void setAuthor(String author) { this.author = author; }

    public List<String> getImageUris() { return imageUris != null ? imageUris : new ArrayList<>(); }
    public void setImageUris(List<String> imageUris) { this.imageUris = imageUris; }

    public String getCoverPhotoUri() { return coverPhotoUri; }
    public void setCoverPhotoUri(String coverPhotoUri) { this.coverPhotoUri = coverPhotoUri; }

    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    public int getStepCount() { return stepCount; }
    public void setStepCount(int stepCount) { this.stepCount = stepCount; }

    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }

    public String getLocationNeighbourhood() { return locationNeighbourhood; }
    public void setLocationNeighbourhood(String locationNeighbourhood) { this.locationNeighbourhood = locationNeighbourhood; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

    public String getWeatherIconCode() { return weatherIconCode; }
    public void setWeatherIconCode(String weatherIconCode) { this.weatherIconCode = weatherIconCode; }

    public List<String> getLinks() { return links != null ? links : new ArrayList<>(); }
    public void setLinks(List<String> links) { this.links = links; }

    public List<String> getVideoUris() { return videoUris != null ? videoUris : new ArrayList<>(); }
    public void setVideoUris(List<String> videoUris) { this.videoUris = videoUris; }

    public String getFontFamily() { return fontFamily != null ? fontFamily : "default"; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public String getEntryPasswordHash() { return entryPasswordHash; }
    public void setEntryPasswordHash(String entryPasswordHash) { this.entryPasswordHash = entryPasswordHash; }

    public boolean isPasswordProtected() { return entryPasswordHash != null && !entryPasswordHash.isEmpty(); }

    public String getHandwritingImagePath() { return handwritingImagePath; }
    public void setHandwritingImagePath(String handwritingImagePath) { this.handwritingImagePath = handwritingImagePath; }
}