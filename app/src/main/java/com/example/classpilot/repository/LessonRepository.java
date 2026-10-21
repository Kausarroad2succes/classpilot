package com.example.classpilot.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.classpilot.data.AppDatabase;
import com.example.classpilot.data.dao.LessonDao;
import com.example.classpilot.data.entity.LessonLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessonRepository {
    private final LessonDao lessonDao;
    // Room writes must not run on the main thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LessonRepository(Application app) {
        lessonDao = AppDatabase.get(app).lessonDao();
    }

    public LiveData<List<LessonLog>> getForClass(String classId) {
        return lessonDao.getForClass(classId);
    }

    public LiveData<LessonLog> getForDate(String classId, long date) {
        return lessonDao.getForDate(classId, date);
    }

    public void save(String classId, long date, String topic, String notes) {
        executor.execute(() -> {
            LessonLog l = new LessonLog();
            l.id = classId + "_" + date;
            l.classId = classId;
            l.topic = topic;
            l.notes = notes;
            l.date = date;
            l.synced = false;
            lessonDao.insert(l);
        });
    }
}