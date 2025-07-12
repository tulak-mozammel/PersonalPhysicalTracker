package com.benessere.personalphysicaltrackerapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.benessere.personalphysicaltrackerapp.db.UserActivitiesDB;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int initialSteps = 0;
    private SharedPreferences sharedPreferences;
    private UserActivitiesDB db;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sharedPreferences = getSharedPreferences("StepData", MODE_PRIVATE);
        db = UserActivitiesDB.getInstance(this);

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialSteps = sharedPreferences.getInt("initialSteps", 0);
        return START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (initialSteps == 0) {
            initialSteps = (int) event.values[0];
            sharedPreferences.edit().putInt("initialSteps", initialSteps).apply();
        }

        int currentSteps = (int) event.values[0] - initialSteps;
        sharedPreferences.edit().putInt("dailySteps", currentSteps).apply();

        // Salva i passi nella riga corretta dell’attività "Walking" attiva
        new Thread(() -> db.userActivityDao().updateFootstepsForCurrentWalking(1, currentSteps)).start();
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}