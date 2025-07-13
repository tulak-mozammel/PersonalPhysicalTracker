package com.benessere.personalphysicaltrackerapp.db;

import androidx.room.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//Capire nel caso sia necessario definire una primary key utilizzando unicamente le colonne:
// (user_id, start_activity, end_activity) la sintassi per fare ciò è la seguente:
//@Entity(primaryKeys = {"firstName", "lastName"})


@Entity(indices = @Index(value = {"user_id", "start_activity", "end_activity"},
        unique = true))
public class UserActivity {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "user_id")
    public int user_id;
    @ColumnInfo(name = "start_activity")
    public Date start_activity;
    @ColumnInfo(name = "end_activity")
    public Date end_activity;
    @ColumnInfo(name = "Type")
    public String Type;
    @ColumnInfo(name = "Footsteps")
    public int Footsteps;

    public UserActivity(String Type) {
        this.user_id = 1;
        this.start_activity = new Date(); // Current date as start activity
        this.end_activity = null; // End activity is null initially
        this.Footsteps = 0;
        this.Type = Type;
    }

    @Override
    public String toString() {
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (end_activity != null) {
            long difference = end_activity.getTime() - start_activity.getTime();
            long minutes = (difference / (1000 * 60)) % 60;
            long hours = (difference / (1000 * 60 * 60)) % 24;

            return "Type: " + Type +
                    " | Date: " + df.format(start_activity) +
                    " | Duration: " + hours + "h " + minutes + "m";
        }

        return "Type: " + Type + " | Start: " + df.format(start_activity);
    }

    public String toStepString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return "Date: " + df.format(start_activity) + " | Steps: " + Footsteps;

    }
}
