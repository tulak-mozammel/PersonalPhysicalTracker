package com.benessere.personalphysicaltrackerapp;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class UserActivityIntentService extends IntentService {
    private ActivityRecognitionClient activityRecognitionClient;
    public UserActivityIntentService() {
        super("UserActivityIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        List<ActivityTransition> transitions = new ArrayList<>();
        int [] detectedActivity =  {DetectedActivity.WALKING, DetectedActivity.IN_VEHICLE, DetectedActivity.STILL, DetectedActivity.ON_BICYCLE};
        int [] transitionActivity =  {ActivityTransition.ACTIVITY_TRANSITION_ENTER, ActivityTransition.ACTIVITY_TRANSITION_EXIT};
        for(int detActivity : detectedActivity){
            for(int transition : transitionActivity){
                transitions.add(new ActivityTransition.Builder()
                        .setActivityType(detActivity)
                        .setActivityTransition(transition)
                        .build());
            }
        }
        // Create a request
        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        // Create a pending intent for our BroadcastReceiver
        Intent transitionIntent = new Intent(this, ActivityTransitionReceiver.class);
        intent.setAction(ActivityTransitionReceiver.INTENT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                transitionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Request updates

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

        }
        activityRecognitionClient = ActivityRecognition.getClient(this);
        Task<Void> task = activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent);
        task.addOnSuccessListener(aVoid -> Log.d("ActivityTransition", "Successfully registered!"));
        task.addOnFailureListener(e -> Log.e("ActivityTransition", "Failed to register", e));
        System.out.print(1);

    }
}