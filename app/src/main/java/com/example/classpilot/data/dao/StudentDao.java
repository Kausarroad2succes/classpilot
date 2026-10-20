package com.example.classpilot.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.classpilot.data.entity.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Student s);

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY roll")
    LiveData<List<Student>> getByClass(String classId);

    @Query("DELETE FROM students WHERE id = :id")
    void delete(String id);
}