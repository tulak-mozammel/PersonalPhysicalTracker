package com.benessere.personalphysicaltrackerapp.db;

import androidx.room.*;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertAll(User... users);

    @Update
    public Completable updateUsers(List<User> users);

    @Delete
    public Completable delete(List<User> users);

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    public Single<List<User>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    public Single<List<User>>  findByName(String first, String last);

    @Query("SELECT * FROM user WHERE uid = :user_id LIMIT 1")
    public Single<List<User>>  findById(int user_id);



}
