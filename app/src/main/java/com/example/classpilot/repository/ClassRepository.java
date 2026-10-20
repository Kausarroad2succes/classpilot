package com.example.classpilot.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.classpilot.data.AppDatabase;
import com.example.classpilot.data.dao.ClassDao;
import com.example.classpilot.data.entity.ClassEntity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassRepository {
    private final ClassDao classDao;
    // Room writes must not run on the main thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ClassRepository(Application app) {
        classDao = AppDatabase.get(app).classDao();
    }

    public LiveData<List<ClassEntity>> getAll() {
        return classDao.getAll();
    }

    public void addClass(String name, String courseCode, int totalTopics) {
        executor.execute(() -> {
            ClassEntity c = new ClassEntity();
            c.id = UUID.randomUUID().toString();
            c.name = name;
            c.courseCode = courseCode;
            c.totalTopics = totalTopics;
            c.joinCode = "";     // generated in the sharing step later
            c.teacherId = "";    // filled after login in the Firebase step
            c.synced = false;
            classDao.insert(c);
        });
    }
}