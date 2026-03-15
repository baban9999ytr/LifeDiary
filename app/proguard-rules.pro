# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in SDK_HOME/tools/proguard/proguard-android.txt.

# Keep Room entities and DAOs (no obfuscation of DB layer)
-keep class com.mustafagoksal.diary.models.** { *; }
-keep class com.mustafagoksal.diary.database.** { *; }

# Keep Serializable models if used with Intents
-keepnames class com.mustafagoksal.diary.models.Users
-keepnames class com.mustafagoksal.diary.models.DiaryEntry
