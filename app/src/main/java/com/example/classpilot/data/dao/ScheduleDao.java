package com.example.classpilot.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.classpilot.data.entity.ScheduleSlot;
import com.example.classpilot.data.entity.SlotWithClass;

import java.util.List;

@Dao
public interface ScheduleDao {
    // REPLACE: inserting a slot with an existing id edits it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScheduleSlot slot);

    @Query("DELETE FROM schedule WHERE id = :id")
    void delete(String id);

    // Week order: Saturday first, Friday last (Saturday = 7, so 7 % 7 = 0).
    // Then earliest class first.
    @Query("SELECT s.*, c.name AS className FROM schedule s " +
            "INNER JOIN classes c ON c.id = s.classId " +
            "ORDER BY (s.dayOfWeek % 7), s.startMinutes")
    LiveData<List<SlotWithClass>> getAll();
}