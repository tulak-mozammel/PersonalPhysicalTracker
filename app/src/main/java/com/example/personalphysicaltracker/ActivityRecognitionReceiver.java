package com.example.personalphysicaltracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ActivityRecognition", "onReceive chiamato");

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            if (result != null) { // Controlla se il risultato non è nullo
                DetectedActivity mostProbableActivity = result.getMostProbableActivity();

                if (mostProbableActivity != null) { // Controlla se l'attività non è nulla
                    int activityType = mostProbableActivity.getType();
                    String activityLabel = getActivityString(activityType);

                    // Log dell'attività rilevata
                    Log.d("ActivityRecognition", "Activity is " + activityLabel);

                    // Invia un Intent locale per aggiornare l'interfaccia utente
                    Intent uiIntent = new Intent("com.example.UPDATE_UI");
                    uiIntent.putExtra("activity_status", "Status: " + (activityType == DetectedActivity.STILL ? "Still" : activityLabel));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(uiIntent);
                } else {
                    Log.d("ActivityRecognition", "Detected activity is null");
                }
            } else {
                Log.d("ActivityRecognition", "ActivityRecognitionResult is null");
            }
        } else {
            Log.d("ActivityRecognition", "No result in intent");
        }
    }

    private String getActivityString(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE: return "In Vehicle";
            case DetectedActivity.STILL: return "Still";
            case DetectedActivity.WALKING: return "Walking";
            case DetectedActivity.RUNNING: return "Running";
            default: return "Unknown";
        }
    }
}
