package com.example.personalphysicaltracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;


public class ActivityRecognitionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            int activityType = mostProbableActivity.getType();
            String activityLabel = getActivityString(activityType);

            // Mostra un messaggio Toast con l'attività rilevata
            Toast.makeText(context, "Detected Activity: " + activityLabel, Toast.LENGTH_SHORT).show();

            // Invia un broadcast per aggiornare l'interfaccia utente
            Intent uiIntent = new Intent("com.example.UPDATE_UI");
            uiIntent.putExtra("activity_status", "Status: " + (activityType == DetectedActivity.STILL ? "Still" : activityLabel));

            // Invia il broadcast tramite LocalBroadcastManager
            LocalBroadcastManager.getInstance(context).sendBroadcast(uiIntent);

            // Log di debug
            Log.d("ActivityRecognition", "Activity is " + activityLabel);
        }
    }

    private String getActivityString(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE: return "In Vehicle";
            case DetectedActivity.STILL: return "Still";
            case DetectedActivity.UNKNOWN: return "Unknown";
            case DetectedActivity.WALKING: return "Walking";
            case DetectedActivity.RUNNING: return "Running";
            default: return "Unknown";
        }
    }
}