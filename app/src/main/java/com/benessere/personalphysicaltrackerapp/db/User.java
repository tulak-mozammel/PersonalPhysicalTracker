package com.benessere.personalphysicaltrackerapp.db;
import androidx.room.*;

import java.util.Date;


@Entity
public class User {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "birthday")
    public Date birthday;
}
