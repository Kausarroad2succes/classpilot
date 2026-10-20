package com.example.classpilot.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.classpilot.data.entity.AttendanceRecord;
import com.example.classpilot.data.entity.StudentPercentage;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AttendanceRecord> records);

    // Marks already saved for one class on one day (to reopen a past session)
    @Query("SELECT * FROM attendance WHERE classId = :classId AND sessionDate = :date")
    LiveData<List<AttendanceRecord>> getForDate(String classId, long date);

    // Semester percentage per student
    @Query("SELECT studentId, " +
            "ROUND(100.0 * SUM(present) / COUNT(*), 1) AS percentage, " +
            "SUM(present) AS presentCount, " +
            "COUNT(*) AS totalCount " +
            "FROM attendance WHERE classId = :classId GROUP BY studentId")
    LiveData<List<StudentPercentage>> getPercentages(String classId);
}