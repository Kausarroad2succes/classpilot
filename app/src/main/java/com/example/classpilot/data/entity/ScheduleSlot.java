package com.example.classpilot.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "schedule",
        foreignKeys = @ForeignKey(
                entity = ClassEntity.class,
                parentColumns = "id",
                childColumns = "classId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("classId")}
)
public class ScheduleSlot {
    @PrimaryKey
    @NonNull
    public String id;          // UUID

    public String classId;
    public String room;
    public int dayOfWeek;      // java.util.Calendar constant: Sunday = 1 ... Saturday = 7
    public int startMinutes;   // minutes since midnight (9:30 AM = 570)
    public boolean synced;
}