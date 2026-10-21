package com.example.classpilot.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "lessons",
        foreignKeys = @ForeignKey(
                entity = ClassEntity.class,
                parentColumns = "id",
                childColumns = "classId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("classId")}
)
public class LessonLog {
    // id = classId + "_" + date, so one lesson per class per day.
    // Saving again on the same day overwrites instead of duplicating.
    @PrimaryKey
    @NonNull
    public String id;

    public String classId;
    public String topic;
    public String notes;
    public long date;        // midnight of that day, in milliseconds
    public boolean synced;
}