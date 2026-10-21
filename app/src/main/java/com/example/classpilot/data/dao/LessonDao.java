package com.example.classpilot.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.classpilot.data.entity.LessonLog;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LessonLog lesson);

    // All lessons of a class, newest first
    @Query("SELECT * FROM lessons WHERE classId = :classId ORDER BY date DESC")
    LiveData<List<LessonLog>> getForClass(String classId);

    // The lesson saved for one day (null when none)
    @Query("SELECT * FROM lessons WHERE classId = :classId AND date = :date")
    LiveData<LessonLog> getForDate(String classId, long date);
}