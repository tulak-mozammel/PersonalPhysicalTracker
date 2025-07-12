package com.benessere.personalphysicaltrackerapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tvDashboard = view.findViewById(R.id.tvDashboard);

        listViewActivities = view.findViewById(R.id.listViewActivities);
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

        dao.findActivitiesByUser(userId).observe(getViewLifecycleOwner(), new Observer<List<UserActivity>>() {
            @Override
            public void onChanged(List<UserActivity> activities) {
                // When the data changes, update the dashboard view.
                if (activities != null) {
                    int totalActivities = activities.size();
                    tvDashboard.setText("Total activities: " + totalActivities);
                } else {
                    tvDashboard.setText("No activities recorded.");
                }
                activityList.clear();
                if (activities != null && !activities.isEmpty()) {
                    for (UserActivity activity : activities) {
                        activityList.add(activity.toString());
                    }
                } else {
                    activityList.add("No activities recorded.");
                }
                adapter.notifyDataSetChanged();
            }
        });

    }
}
