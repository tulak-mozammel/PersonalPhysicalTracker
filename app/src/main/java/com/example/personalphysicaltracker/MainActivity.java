package com.example.personalphysicaltracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    private Button startTrackingButton;
    private Button stopTrackingButton;
    private TextView activityStatusText;
    private BroadcastReceiver activityStatusReceiver;
    private static final int REQUEST_ACTIVITY_RECOGNITION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTrackingButton = findViewById(R.id.startTrackingButton);
        stopTrackingButton = findViewById(R.id.stopTrackingButton);
        activityStatusText = findViewById(R.id.activityStatusText);

        // Imposta il BroadcastReceiver per aggiornare l'UI
        activityStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ActivityRecognition", "onReceive chiamato");
                String activityStatus = intent.getStringExtra("activity_status");
                if (activityStatus != null) {

                    activityStatusText.setText(activityStatus); // Aggiorna la TextView
                }
            }
        };

        // Registra il BroadcastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
                activityStatusReceiver, new IntentFilter("com.example.UPDATE_UI"));

        // Richiedi il permesso ACTIVITY_RECOGNITION per Android 10 e superiori
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_ACTIVITY_RECOGNITION);
            }
        }


        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityStatusReceiver);
    }

    private void startTracking() {
        Intent intent = new Intent(this, BackgroundDetectedActivitiesService.class);
        startService(intent);
        activityStatusText.setText("Status: Tracking started");
        Log.d("button", "Tracking iniziato");
    }

    private void stopTracking() {
        Intent intent = new Intent(this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
        activityStatusText.setText("Status: Tracking stopped");
        Log.d("button", "Tracking stopped");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with activity recognition setup
            } else {
                // Permission denied, show a message to the user
            }
        }
    }
}
