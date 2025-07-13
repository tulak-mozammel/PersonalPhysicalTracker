package com.benessere.personalphysicaltrackerapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.benessere.personalphysicaltrackerapp.db.UserActivity;
import com.benessere.personalphysicaltrackerapp.db.UserActivityDao;
import com.benessere.personalphysicaltrackerapp.db.UserActivitiesDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button btnDashboard;
    private Button btnFirst;
    private Button btnStartStill;
    private Button btnStartWalking;
    private Button btnStartDriving;
    private Button btnStopActivity;
    private Button btnopenReport;

    private UserActivityDao userActivityDao;
    private int userId = 1; // o recuperato dinamicamente

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private String[] requiredPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermission();

        UserActivitiesDB db = UserActivitiesDB.getInstance(this);
        userActivityDao = db.userActivityDao();
        Log.d("DB", "userActivityDao inizializzato: " + (userActivityDao != null));

        btnDashboard = findViewById(R.id.btnDashboard);
        btnFirst = findViewById(R.id.btnFirst);
        btnStartStill = findViewById(R.id.btnStartStill);
        btnStartWalking = findViewById(R.id.btnStartWalking);
        btnStartDriving = findViewById(R.id.btnStartDriving);
        btnStopActivity = findViewById(R.id.btnStopActivity);
        btnopenReport = findViewById(R.id.openReportBtn);

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new DashboardFragment());
            }
        });

        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FirstFragment());
            }
        });

        btnopenReport.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityReport.class);
            intent.putExtra("userId", 1); // o il tuo userId dinamico
            startActivity(intent);
        });

        btnStartStill.setOnClickListener(v -> {
            stopCurrentActivity(); // chiude quella precedente
            startActivity("Still");
            Toast.makeText(this, "Started Still", Toast.LENGTH_SHORT).show();
        });

        btnStartWalking.setOnClickListener(v -> {
            stopCurrentActivity(); // chiude quella precedente
            startActivity("Walking");
            startService(new Intent(this, StepCounterService.class)); // avvia contapassi
            Toast.makeText(this, "Started Walking", Toast.LENGTH_SHORT).show();
        });

        btnStartDriving.setOnClickListener(v -> {
            stopCurrentActivity();// chiude quella precedente
            startActivity("Driving");
            Toast.makeText(this, "Started Driving", Toast.LENGTH_SHORT).show();
        });

        btnStopActivity.setOnClickListener(v -> {
            stopCurrentActivity();
        });

        // Richiesta permesso per notifiche (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Avvia notifiche periodiche ogni 6 ore
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 6, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(this).enqueue(workRequest);


        if (savedInstanceState == null) {
            replaceFragment(new DashboardFragment());
        }


    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requiredPermissions = new String[] {
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.BODY_SENSORS  // Remove if not needed
            };
        } else {
            requiredPermissions = new String[] {};
        }

        if (!hasPermissions(this, requiredPermissions)) {
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQUEST_CODE);
        } else {
            startActivityUpdates();
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) return true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "Required permissions not granted. The app may not work correctly.", Toast.LENGTH_LONG).show();

            } else {

            }
        }
    }

    private void startActivityUpdates() {
        Intent serviceIntent = new Intent(this, UserActivityIntentService.class);
        startService(serviceIntent);
    }

    private void startActivity(String activityType) {
        new Thread(() -> {
            Date now = new Date();
            UserActivity lastActivity = userActivityDao.getLastClosedActivity(userId); // recupera la query

            if (lastActivity != null && lastActivity.end_activity != null) {
                long gapMillis = now.getTime() - lastActivity.end_activity.getTime();
                long tenMinutes = 10 * 60 * 1000;

                if (gapMillis > tenMinutes) {
                    // Inserisci attività "Unknown"
                    UserActivity unknown = new UserActivity("Unknown");
                    unknown.user_id = userId;
                    unknown.start_activity = lastActivity.end_activity;
                    unknown.end_activity = now;

                    userActivityDao.insertAll(List.of(unknown));
                    Log.d("MainActivity", "Attività Unknown inserita per colmare il buco");
                }
            }

            //inserisce l'attività nuova (con start_activity = now)
            UserActivity activity = new UserActivity(activityType);
            activity.user_id = userId;
            activity.start_activity = now;

            userActivityDao.insertAll(List.of(activity));

            runOnUiThread(() -> Toast.makeText(this, "Started " + activityType, Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void endCurrentActivity() {
        Date endTime = new Date(); // Usa java.util.Date,
        new Thread(() -> userActivityDao.endLastActivity(userId, endTime)).start();
    }

    private void stopCurrentActivity() {
        new Thread(() -> {
            // Recupera l’ultima attività non conclusa
            UserActivity last = userActivityDao.getLastOpenActivity(userId);
            if (last != null) {
                last.end_activity = new Date();
                userActivityDao.update(last);

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Attività \"" + last.Type + "\" terminata", Toast.LENGTH_SHORT).show();

                    // Se era walking, ferma il contapassi
                    if ("Walking".equalsIgnoreCase(last.Type)) {
                        stopService(new Intent(MainActivity.this, StepCounterService.class));
                    }
                });
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Nessuna attività da terminare", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
