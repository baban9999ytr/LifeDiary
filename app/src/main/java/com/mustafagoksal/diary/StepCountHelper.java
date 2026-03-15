package com.mustafagoksal.diary;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepCountHelper implements SensorEventListener {

    public interface StepCallback {
        void onSteps(int steps);
        void onUnavailable();
    }

    private final SensorManager sensorManager;
    private final StepCallback callback;
    private final Context context;
    private Sensor stepSensor;

    public StepCountHelper(Context context, StepCallback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;
        sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void readOnce() {
        if (stepSensor == null) {
            callback.onUnavailable();
            return;
        }
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int totalSinceBoot = (int) event.values[0];
        sensorManager.unregisterListener(this);

        int baseline = com.mustafagoksal.diary.StepPrefs.getDailyBaseline(context);


        if (baseline == 0) {
            com.mustafagoksal.diary.StepPrefs.setBaseline(context, totalSinceBoot);
        }

        int dailySteps = Math.max(0, totalSinceBoot - baseline);
        callback.onSteps(dailySteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}