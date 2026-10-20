package com.example.classpilot.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "attendance",
        indices = {@Index("classId"), @Index("studentId")}
)
public class AttendanceRecord {
    // id = studentId + "_" + sessionDate, so saving the same day twice
    // overwrites the old mark instead of creating a duplicate.
    @PrimaryKey
    @NonNull
    public String id;

    public String classId;
    public String studentId;
    public long sessionDate;   // midnight of that day, in milliseconds
    public boolean present;
    public boolean synced;
}