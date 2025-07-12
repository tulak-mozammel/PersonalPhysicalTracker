package com.benessere.personalphysicaltrackerapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.benessere.personalphysicaltrackerapp.db.UserActivitiesDB;
import com.benessere.personalphysicaltrackerapp.db.UserActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityReport extends AppCompatActivity {

    private PieChart pieChart;
    private LineChart lineChart;
    private UserActivitiesDB db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        db = UserActivitiesDB.getInstance(this);

        // Calcola inizio mese
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startOfMonth = cal.getTimeInMillis();

        int userId = 1; // Inserisci l'ID utente giusto

        // Osserva LiveData
        db.userActivityDao().getActivitiesFromMonth(userId, startOfMonth).observe(this, new Observer<List<UserActivity>>() {
            @Override
            public void onChanged(List<UserActivity> activities) {
                updatePieChart(activities);
                updateLineChart(activities);
            }
        });
    }

    private void updatePieChart(List<UserActivity> activities) {
        long walkingTime = 0;
        long stillTime = 0;
        long drivingTime = 0;

        for (UserActivity activity : activities) {
            if (activity.end_activity != null && activity.start_activity != null) {
                long duration = activity.end_activity.getTime() - activity.start_activity.getTime();

                if ("Walking".equals(activity.Type)) {
                    walkingTime += duration;
                } else if ("Still".equals(activity.Type)) {
                    stillTime += duration;
                }
             else if ("Driving".equalsIgnoreCase(activity.Type)) {
                drivingTime += duration;
            }
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        if (walkingTime > 0) entries.add(new PieEntry(walkingTime, "Walking"));
        if (stillTime > 0) entries.add(new PieEntry(stillTime, "Still"));
        if (drivingTime > 0) entries.add(new PieEntry(drivingTime, "Driving"));

        PieDataSet dataSet = new PieDataSet(entries, "Attività Mese");
        dataSet.setColors(Color.GREEN, Color.GRAY, Color.BLUE); // Colori per ogni attività
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Attività Del Mese");
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.invalidate(); // Refresh del grafico
    }

    private void updateLineChart(List<UserActivity> activities) {
        // Mappa dei passi per giorno (giorno → somma dei passi)
        int[] dailySteps = new int[31]; // da indice 0 a 30 (giorni 1–31)

        for (UserActivity activity : activities) {
            if (activity.Type.equals("Walking") && activity.start_activity != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(activity.start_activity);
                int day = cal.get(Calendar.DAY_OF_MONTH); // da 1 a 31
                if (day >= 1 && day <= 31) {
                    dailySteps[day - 1] += activity.Footsteps; // somma i passi
                }
            }
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            if (dailySteps[i] > 0) {
                entries.add(new Entry(i + 1, dailySteps[i])); // X = giorno, Y = passi
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Passi giornalieri");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setCircleColor(Color.MAGENTA);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("Passi nel mese");
        lineChart.invalidate(); // refresh
    }

}


