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
import com.benessere.personalphysicaltrackerapp.db.UserActivitiesDB;
import com.benessere.personalphysicaltrackerapp.db.UserActivity;
import com.benessere.personalphysicaltrackerapp.db.UserActivityDao;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private ListView listViewActivities;
    private ArrayAdapter<String> adapter;
    private List<String> activityList;
    private UserActivityDao dao;

    private int userId = 1;

    public FirstFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        listViewActivities = view.findViewById(R.id.listViewSteps);

        activityList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, activityList);
        listViewActivities.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserActivitiesDB db = UserActivitiesDB.getInstance(requireContext());
        dao = db.userActivityDao();

        dao.findStepsByUser(userId, "Walking").observe(getViewLifecycleOwner(), new Observer<List<UserActivity>>() {
            @Override
            public void onChanged(List<UserActivity> activities) {
                activityList.clear();
                if (activities != null && !activities.isEmpty()) {
                    for (UserActivity activity : activities) {
                        activityList.add(activity.toStepString());
                    }
                } else {
                    activityList.add("No walks recorded.");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
