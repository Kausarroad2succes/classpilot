package com.example.classpilot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.classpilot.data.entity.ClassEntity;
import com.example.classpilot.data.entity.ScheduleSlot;
import com.example.classpilot.data.entity.SlotWithClass;
import com.example.classpilot.repository.ClassRepository;
import com.example.classpilot.repository.ScheduleRepository;

import java.util.List;

public class RoutineViewModel extends AndroidViewModel {
    private final ScheduleRepository scheduleRepository;
    private final LiveData<List<SlotWithClass>> slots;
    private final LiveData<List<ClassEntity>> classes;

    public RoutineViewModel(@NonNull Application application) {
        super(application);
        scheduleRepository = new ScheduleRepository(application);
        ClassRepository classRepository = new ClassRepository(application);
        slots = scheduleRepository.getAll();
        classes = classRepository.getAll();
    }

    public LiveData<List<SlotWithClass>> getSlots() { return slots; }
    public LiveData<List<ClassEntity>> getClasses() { return classes; }

    public void save(ScheduleSlot slot) { scheduleRepository.save(slot); }
    public void delete(String id) { scheduleRepository.delete(id); }
}