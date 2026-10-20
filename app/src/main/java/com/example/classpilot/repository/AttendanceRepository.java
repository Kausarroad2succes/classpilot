package com.example.classpilot.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.classpilot.data.AppDatabase;
import com.example.classpilot.data.dao.AttendanceDao;
import com.example.classpilot.data.entity.AttendanceRecord;
import com.example.classpilot.data.entity.Student;
import com.example.classpilot.data.entity.StudentPercentage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttendanceRepository {
    private final AttendanceDao attendanceDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AttendanceRepository(Application app) {
        attendanceDao = AppDatabase.get(app).attendanceDao();
    }

    public LiveData<List<AttendanceRecord>> getForDate(String classId, long date) {
        return attendanceDao.getForDate(classId, date);
    }

    public LiveData<List<StudentPercentage>> getPercentages(String classId) {
        return attendanceDao.getPercentages(classId);
    }

    public void save(String classId, long date, List<Student> students,
                     Map<String, Boolean> marks) {
        // Copy the data first so the background thread has its own safe copy
        List<Student> studentsCopy = new ArrayList<>(students);
        Map<String, Boolean> marksCopy = new HashMap<>(marks);

        executor.execute(() -> {
            List<AttendanceRecord> records = new ArrayList<>();
            for (Student s : studentsCopy) {
                AttendanceRecord r = new AttendanceRecord();
                r.id = s.id + "_" + date;
                r.classId = classId;
                r.studentId = s.id;
                r.sessionDate = date;
                Boolean mark = marksCopy.get(s.id);
                r.present = (mark == null) || mark;   // default = present
                r.synced = false;
                records.add(r);
            }
            attendanceDao.insertAll(records);
        });
    }
}