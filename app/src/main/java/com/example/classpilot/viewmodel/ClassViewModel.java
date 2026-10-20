package com.example.classpilot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.classpilot.data.entity.ClassEntity;
import com.example.classpilot.repository.ClassRepository;

import java.util.List;

public class ClassViewModel extends AndroidViewModel {
    private final ClassRepository repository;
    private final LiveData<List<ClassEntity>> classes;

    public ClassViewModel(@NonNull Application application) {
        super(application);
        repository = new ClassRepository(application);
        classes = repository.getAll();
    }

    public LiveData<List<ClassEntity>> getClasses() {
        return classes;
    }

    public void addClass(String name, String courseCode, int totalTopics) {
        repository.addClass(name, courseCode, totalTopics);
    }
}