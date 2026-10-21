package com.example.classpilot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.classpilot.data.entity.LessonLog;
import com.example.classpilot.repository.LessonRepository;

import java.util.List;

public class LessonViewModel extends AndroidViewModel {

    private final LessonRepository repository;

    private String classId;
    private long sessionDate;
    private LiveData<List<LessonLog>> lessons;
    private LiveData<LessonLog> todayLesson;
    private boolean prefilled = false;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        repository = new LessonRepository(application);
    }

    public void init(String classId, long sessionDate) {
        if (this.classId != null) return;
        this.classId = classId;
        this.sessionDate = sessionDate;
        lessons = repository.getForClass(classId);
        todayLesson = repository.getForDate(classId, sessionDate);
    }

    public LiveData<List<LessonLog>> getLessons() { return lessons; }
    public LiveData<LessonLog> getTodayLesson() { return todayLesson; }

    public boolean isPrefilled() { return prefilled; }
    public void setPrefilled() { prefilled = true; }

    public void save(String topic, String notes) {
        repository.save(classId, sessionDate, topic, notes);
    }
}