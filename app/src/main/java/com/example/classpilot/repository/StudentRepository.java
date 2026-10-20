package com.example.classpilot.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.classpilot.data.AppDatabase;
import com.example.classpilot.data.dao.StudentDao;
import com.example.classpilot.data.entity.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentRepository {
    private final StudentDao studentDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public StudentRepository(Application app) {
        studentDao = AppDatabase.get(app).studentDao();
    }

    public LiveData<List<Student>> getByClass(String classId) {
        return studentDao.getByClass(classId);
    }

    // Creates students with roll 1..count and no name
    public void createRolls(String classId, int count) {
        executor.execute(() -> {
            List<Student> list = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                Student s = new Student();
                s.id = UUID.randomUUID().toString();
                s.classId = classId;
                s.roll = String.valueOf(i);
                s.name = "";
                s.synced = false;
                list.add(s);
            }
            studentDao.insertAll(list);
        });
    }

    public void addStudent(String classId, String name, String roll) {
        executor.execute(() -> {
            Student s = new Student();
            s.id = UUID.randomUUID().toString();
            s.classId = classId;
            s.name = name;
            s.roll = roll;
            s.synced = false;
            studentDao.insert(s);
        });
    }

    public void deleteStudent(String studentId) {
        executor.execute(() -> studentDao.delete(studentId));
    }
}