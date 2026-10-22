package com.example.classpilot.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.classpilot.data.AppDatabase;
import com.example.classpilot.data.dao.ScheduleDao;
import com.example.classpilot.data.entity.ScheduleSlot;
import com.example.classpilot.data.entity.SlotWithClass;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleRepository {
    private final ScheduleDao scheduleDao;
    // Room writes must not run on the main thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScheduleRepository(Application app) {
        scheduleDao = AppDatabase.get(app).scheduleDao();
    }

    public LiveData<List<SlotWithClass>> getAll() {
        return scheduleDao.getAll();
    }

    public void save(ScheduleSlot slot) {
        executor.execute(() -> scheduleDao.insert(slot));
    }

    public void delete(String id) {
        executor.execute(() -> scheduleDao.delete(id));
    }
}