package com.benessere.personalphysicaltrackerapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.Date;
import java.util.List;

@Dao
public interface UserActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserActivity> activities);

    @Delete
    void delete(List<UserActivity> activities);

    @Query("SELECT * FROM UserActivity WHERE uid IN (:userIds)")
    public LiveData<List<UserActivity>> loadAllByIds(int[] userIds);
    @Query("SELECT * FROM UserActivity WHERE user_id = :user_id ORDER BY start_activity DESC")
    LiveData<List<UserActivity>> findActivitiesByUser(int user_id);

    @Query("SELECT * FROM UserActivity WHERE user_id = :userId AND Type = :type ORDER BY start_activity DESC")
    LiveData<List<UserActivity>> findStepsByUser(int userId, String type);

    @Query("UPDATE UserActivity SET end_activity = current_timestamp, " +
            "Footsteps =  :footsteps " +
            "WHERE uid = (SELECT MAX(uid) FROM UserActivity WHERE user_id= :user_id)")
    public void endLastActivity(int user_id, int footsteps);

    @Query("UPDATE UserActivity SET end_activity = :endTime WHERE user_id = :userId AND end_activity IS NULL")
    void endLastActivity(int userId, Date endTime);
    @Query("UPDATE UserActivity SET Footsteps = :steps WHERE user_id = :userId AND Type = 'Walking' AND end_activity IS NULL")
    void updateFootstepsForCurrentWalking(int userId, int steps);

    @Query("SELECT * FROM UserActivity WHERE user_id = :userId AND end_activity IS NULL ORDER BY start_activity DESC LIMIT 1")
    UserActivity getLastOpenActivity(int userId);

    @Query("SELECT * FROM UserActivity WHERE user_id = :userId AND end_activity IS NOT NULL ORDER BY end_activity DESC LIMIT 1")
    UserActivity getLastClosedActivity(int userId);

    @Query("SELECT * FROM UserActivity WHERE user_id = :userId AND start_activity >= :startOfMonth")
    LiveData<List<UserActivity>> getActivitiesFromMonth(int userId, long startOfMonth);


    @Update
    void update(UserActivity activity);

}