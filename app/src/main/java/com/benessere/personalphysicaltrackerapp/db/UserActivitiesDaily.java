package com.benessere.personalphysicaltrackerapp.db;
import androidx.room.*;

import java.util.Date;

@DatabaseView("SELECT user_id AS  user_id, " +
    "Type AS  activity_type," +
   "start_activity AS  activity_dt," +
   "sum(end_activity - start_activity) AS  activity_time, " +
        "sum(Footsteps) as daily_footsteps " +
   "from UserActivity group by user_id, activity_dt, type, start_activity" )

public class UserActivitiesDaily {
    public int user_id;
    public String activity_type;
    public Date activity_dt;
    public long activity_time;

    public int daily_footsteps;
}
