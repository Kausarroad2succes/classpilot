package com.example.classpilot.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.classpilot.data.AppDatabase;
import com.example.classpilot.data.dao.ClassDao;
import com.example.classpilot.data.dao.StudentDao;
import com.example.classpilot.data.entity.ClassEntity;
import com.example.classpilot.data.entity.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassRepository {
    private final ClassDao classDao;
    private final StudentDao studentDao;
    // Room writes must not run on the main thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ClassRepository(Application app) {
        AppDatabase db = AppDatabase.get(app);
        classDao = db.classDao();
        studentDao = db.studentDao();
    }

    public LiveData<List<ClassEntity>> getAll() {
        return classDao.getAll();
    }

    // Saves the class, then creates rolls 1..studentCount (names stay empty).
    // Both steps run one after the other on the same background thread, so the
    // class row always exists before the students that point to it.
    public void addClass(String name, String courseCode, int studentCount) {
        executor.execute(() -> {
            ClassEntity c = new ClassEntity();
            c.id = UUID.randomUUID().toString();
            c.name = name;
            c.courseCode = courseCode;
            c.totalTopics = 0;   // no longer used (the teacher does not know the total)
            c.joinCode = "";     // generated in the sharing step later
            c.teacherId = "";    // filled after login in the Firebase step
            c.synced = false;
            classDao.insert(c);

            List<Student> rolls = new ArrayList<>();
            for (int i = 1; i <= studentCount; i++) {
                Student s = new Student();
                s.id = UUID.randomUUID().toString();
                s.classId = c.id;
                s.roll = String.valueOf(i);
                s.name = "";
                s.synced = false;
                rolls.add(s);
            }
            studentDao.insertAll(rolls);
        });
    }
}