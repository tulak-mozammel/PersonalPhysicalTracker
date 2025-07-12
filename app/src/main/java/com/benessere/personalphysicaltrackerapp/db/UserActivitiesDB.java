package com.benessere.personalphysicaltrackerapp.db;

import android.content.Context;

import androidx.room.RoomDatabase;
import androidx.room.*;

@Database(entities = {User.class, UserActivity.class},views = {UserActivitiesDaily.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class UserActivitiesDB extends RoomDatabase {
        // Singleton instance to prevent having multiple instances of the database opened at the same time.
        private static volatile UserActivitiesDB INSTANCE;
        public abstract UserDao userDao();
        public abstract UserActivityDao userActivityDao();


        public static UserActivitiesDB getInstance(Context context) {
                if (INSTANCE == null) {
                        synchronized (UserActivitiesDB.class) {
                                if (INSTANCE == null) {
                                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                        UserActivitiesDB.class, "user_activity_database")
                                                .fallbackToDestructiveMigration()
                                                .build();
                                }
                        }
                }
                return INSTANCE;
        }

}
