package com.mustafagoksal.diary.location;

public interface LocationCallback {
    void onLocationFetched(double latitude, double longitude);
    void onLocationFailed();
}
