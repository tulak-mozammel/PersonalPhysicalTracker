package com.benessere.personalphysicaltrackerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.benessere.personalphysicaltrackerapp.db.UserActivitiesDB;
import com.benessere.personalphysicaltrackerapp.db.UserActivity;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityTransitionReceiver extends BroadcastReceiver {
    private static UserActivitiesDB db;
    public static final String INTENT_ACTION = "com.benessere.personalphysicaltrackerapp.ACTION_PROCESS_ACTIVITY_TRANSITIONS";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            db = UserActivitiesDB.getInstance(context);
            for (ActivityTransitionEvent event : Objects.requireNonNull(result).getTransitionEvents()) {
                int activityType = event.getActivityType();
                int transitionType = event.getTransitionType();

                String activity = "Unknown";

                switch (activityType) {
                    case DetectedActivity.IN_VEHICLE:
                        activity = "Driving";
                        break;
                    case DetectedActivity.WALKING:
                    case DetectedActivity.ON_FOOT:
                        activity = "Walking";
                        break;
                    case DetectedActivity.STILL:
                        activity = "Still";
                        break;
                    default:
                        activity = "Unknown";
                }

                if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    Toast.makeText(context, "Started " + activity, Toast.LENGTH_SHORT).show();
                    registerStartActivity(activity);

                    if (activity.equals("Walking")) {
                        // Start step counter service when user starts walking
                        Intent stepService = new Intent(context, StepCounterService.class);
                        context.startService(stepService);

                    }
                } else if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                    Toast.makeText(context, "Stopped " + activity, Toast.LENGTH_SHORT).show();
                    registerEndActivity(activity);

                }
            }
        }
        Toast.makeText(context, "Stopped " , Toast.LENGTH_SHORT).show();

    }
    private static void registerStartActivity(String activityType){
        UserActivity activity = new UserActivity(activityType);
        List<UserActivity> activityList = new ArrayList<>();
        activityList.add(activity);
        new Thread(() -> db.userActivityDao().insertAll(activityList)).start();
    }

    private static void registerEndActivity(String activityType){
        new Thread(() -> db.userActivityDao().endLastActivity(1,0)).start();
    }
}
