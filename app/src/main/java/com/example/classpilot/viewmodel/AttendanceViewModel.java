package com.example.classpilot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.classpilot.data.entity.AttendanceRecord;
import com.example.classpilot.data.entity.Student;
import com.example.classpilot.data.entity.StudentPercentage;
import com.example.classpilot.repository.AttendanceRepository;
import com.example.classpilot.repository.StudentRepository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceViewModel extends AndroidViewModel {
    public static final int PAGE_SIZE = 10;
    // true  = everyone starts ticked, teacher unticks absent students
    // false = everyone starts unticked, teacher ticks present students
    public static final boolean DEFAULT_PRESENT = true;

    private final StudentRepository studentRepo;
    private final AttendanceRepository attendanceRepo;

    private String classId;
    private long sessionDate;
    private LiveData<List<Student>> students;
    private LiveData<List<StudentPercentage>> percentages;
    private LiveData<List<AttendanceRecord>> todayRecords;

    private final Map<String, Boolean> marks = new HashMap<>();
    private boolean savedMarksLoaded = false;
    private boolean countPrompted = false;
    private int page = 0;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        studentRepo = new StudentRepository(application);
        attendanceRepo = new AttendanceRepository(application);
    }

    public void init(String classId) {
        if (this.classId != null) return;
        this.classId = classId;
        this.sessionDate = todayMidnight();
        students = studentRepo.getByClass(classId);
        percentages = attendanceRepo.getPercentages(classId);
        todayRecords = attendanceRepo.getForDate(classId, sessionDate);
    }

    private long todayMidnight() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public LiveData<List<Student>> getStudents() { return students; }
    public LiveData<List<StudentPercentage>> getPercentages() { return percentages; }
    public LiveData<List<AttendanceRecord>> getTodayRecords() { return todayRecords; }
    public long getSessionDate() { return sessionDate; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public boolean isCountPrompted() { return countPrompted; }
    public void setCountPrompted() { countPrompted = true; }

    public boolean isPresent(String studentId) {
        Boolean m = marks.get(studentId);
        return m == null ? DEFAULT_PRESENT : m;
    }

    public void setPresent(String studentId, boolean present) {
        marks.put(studentId, present);
    }

    public void toggle(String studentId) {
        marks.put(studentId, !isPresent(studentId));
    }

    public boolean isSavedMarksLoaded() { return savedMarksLoaded; }

    public void loadSavedMarks(List<AttendanceRecord> records) {
        savedMarksLoaded = true;
        for (AttendanceRecord r : records) {
            marks.put(r.studentId, r.present);
        }
    }

    public void save(List<Student> currentStudents) {
        // Fill in default for students nobody touched, so all rows are saved consistently
        Map<String, Boolean> full = new HashMap<>();
        for (Student s : currentStudents) {
            full.put(s.id, isPresent(s.id));
        }
        attendanceRepo.save(classId, sessionDate, currentStudents, full);
    }

    public void createRolls(int count) {
        studentRepo.createRolls(classId, count);
    }
}
