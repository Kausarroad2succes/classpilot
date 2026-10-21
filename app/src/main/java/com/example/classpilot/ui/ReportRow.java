package com.example.classpilot.ui;

// One line in the Overall / Lowest attendance list
public class ReportRow {
    public final String studentId;
    public final String roll;
    public final String name;
    public final boolean hasRecord;   // false = no attendance saved for this student yet
    public final double percentage;
    public final int presentCount;
    public final int totalCount;

    public ReportRow(String studentId, String roll, String name, boolean hasRecord,
                     double percentage, int presentCount, int totalCount) {
        this.studentId = studentId;
        this.roll = roll;
        this.name = name;
        this.hasRecord = hasRecord;
        this.percentage = percentage;
        this.presentCount = presentCount;
        this.totalCount = totalCount;
    }
}