package com.example.personalphysicaltracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;


public class ActivityRecognitionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                String activityType = getActivityString(event.getActivityType());
                Toast.makeText(context, "Detected Activity: " + activityType, Toast.LENGTH_SHORT).show();
            }
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