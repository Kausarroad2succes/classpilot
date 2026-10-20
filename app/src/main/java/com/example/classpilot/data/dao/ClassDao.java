package com.example.classpilot.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.classpilot.data.entity.ClassEntity;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClassEntity c);

    @Query("SELECT * FROM classes ORDER BY name")
    LiveData<List<ClassEntity>> getAll();

    @Query("SELECT * FROM classes WHERE id = :id")
    LiveData<ClassEntity> getById(String id);
}