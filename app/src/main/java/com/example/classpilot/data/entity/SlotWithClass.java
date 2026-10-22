package com.example.classpilot.data.entity;

import androidx.room.Embedded;

/** Result of a JOIN query (NOT a table): one routine slot plus its class name. */
public class SlotWithClass {
    @Embedded
    public ScheduleSlot slot;

    public String className;
}