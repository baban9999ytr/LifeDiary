package com.example.gunluk.models;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class Users implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int userID = 0;

    @ColumnInfo(name = "username")
    String username = "";

    @ColumnInfo(name = "email")
    String email = "";

    @ColumnInfo(name = "password")
    String password = "";

    @ColumnInfo(name = "profile_photo_uri")
    String profilePhotoUri = "";

    @ColumnInfo(name = "verificationCode")
    String verificationCode = "";




    public int getUserID()                       { return userID; }
    public void setUserID(int userID)            { this.userID = userID; }

    public String getUsername()                      { return username; }
    public void setUsername(String username)         { this.username = username; }

    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }

    public String getPassword()                      { return password; }
    public void setPassword(String password)         { this.password = password; }

    public String getProfilePhotoUri()                           { return profilePhotoUri; }
    public void setProfilePhotoUri(String profilePhotoUri)       { this.profilePhotoUri = profilePhotoUri; }

    public String getVerificationCode()                          { return verificationCode; }
    public void setVerificationCode(String verificationCode)     { this.verificationCode = verificationCode; }
}
