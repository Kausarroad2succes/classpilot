package com.example.classpilot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.classpilot.data.entity.Student;
import com.example.classpilot.data.entity.StudentPercentage;
import com.example.classpilot.repository.AttendanceRepository;
import com.example.classpilot.repository.StudentRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {
    private final StudentRepository studentRepo;
    private final AttendanceRepository attendanceRepo;

    private String classId;
    private LiveData<List<Student>> students;
    private LiveData<List<StudentPercentage>> percentages;

    // Lowest screen filter: true = only students below 75%
    private boolean belowOnly = true;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        studentRepo = new StudentRepository(application);
        attendanceRepo = new AttendanceRepository(application);
    }

    public void init(String classId) {
        if (this.classId != null) return;
        this.classId = classId;
        students = studentRepo.getByClass(classId);
        percentages = attendanceRepo.getPercentages(classId);
    }

    public LiveData<List<Student>> getStudents() { return students; }
    public LiveData<List<StudentPercentage>> getPercentages() { return percentages; }

    public boolean isBelowOnly() { return belowOnly; }
    public void setBelowOnly(boolean belowOnly) { this.belowOnly = belowOnly; }
}