package com.example.classpilot.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "students",
        foreignKeys = @ForeignKey(
                entity = ClassEntity.class,
                parentColumns = "id",
                childColumns = "classId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("classId")}
)
public class Student {
    @PrimaryKey
    @NonNull
    public String id;

    public String classId;
    public String name;
    public String roll;
    public boolean synced;
}