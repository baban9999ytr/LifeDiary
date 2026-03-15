# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in SDK_HOME/tools/proguard/proguard-android.txt.

# Keep Room entities and DAOs (no obfuscation of DB layer)
-keep class com.example.gunluk.models.** { *; }
-keep class com.example.gunluk.database.** { *; }

# Keep Serializable models if used with Intents
-keepnames class com.example.gunluk.models.Users
-keepnames class com.example.gunluk.models.DiaryEntry
