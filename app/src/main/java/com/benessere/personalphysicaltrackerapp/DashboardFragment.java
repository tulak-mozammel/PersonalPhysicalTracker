package com.benessere.personalphysicaltrackerapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.benessere.personalphysicaltrackerapp.db.*;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView tvDashboard;
    private ListView listViewActivities;
    private ArrayAdapter<String> adapter;
    private List<String> activityList;
    private UserActivityDao dao;
    private int userId = 1;
    private RadioGroup radioGroupFilter;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvDashboard = view.findViewById(R.id.tvDashboard);
        listViewActivities = view.findViewById(R.id.listViewActivities);
        radioGroupFilter = view.findViewById(R.id.radioGroupFilter);

        activityList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, activityList);
        listViewActivities.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserActivitiesDB db = UserActivitiesDB.getInstance(getContext());
        dao = db.userActivityDao();

        // Carica tutto all'inizio (All)
        loadActivities(null);

        // Listener sul filtro
        radioGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            String type = null;
            if (checkedId == R.id.radioWalking) type = "Walking";
            else if (checkedId == R.id.radioDriving) type = "Driving";
            else if (checkedId == R.id.radioStill) type = "Still";
            // Se radioAll è selezionato, type rimane null

            loadActivities(type);
        });
    }

    private void loadActivities(@Nullable String type) {
        if (type == null) {
            dao.findActivitiesByUser(userId).observe(getViewLifecycleOwner(), activities -> {
                tvDashboard.setText(activities != null ? "Total activities: " + activities.size() : "No activities recorded.");
                activityList.clear();
                if (activities != null && !activities.isEmpty()) {
                    for (UserActivity activity : activities) {
                        activityList.add(activity.toString());
                    }
                } else {
                    activityList.add("No activities recorded.");
                }
                adapter.notifyDataSetChanged();
            });
        } else {
            dao.findStepsByUser(userId, type).observe(getViewLifecycleOwner(), activities -> {
                tvDashboard.setText(activities != null ? "Total activities: " + activities.size() : "No activities recorded.");
                activityList.clear();
                if (activities != null && !activities.isEmpty()) {
                    for (UserActivity activity : activities) {
                        activityList.add(activity.toString());
                    }
                } else {
                    activityList.add("No activities recorded.");
                }
                adapter.notifyDataSetChanged();
            });
        }
    }
}
