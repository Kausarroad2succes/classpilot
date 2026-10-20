package com.example.classpilot.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class ClassEntity {
    @PrimaryKey
    @NonNull
    public String id;          // UUID, so it works offline and later in Firestore

    public String name;
    public String courseCode;
    public String joinCode;    // used in the sharing step later
    public String teacherId;
    public int totalTopics;    // used for syllabus progress later
    public boolean synced;
}