package com.example.classpilot.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.classpilot.data.dao.AttendanceDao;
import com.example.classpilot.data.dao.ClassDao;
import com.example.classpilot.data.dao.LessonDao;
import com.example.classpilot.data.dao.StudentDao;
import com.example.classpilot.data.entity.AttendanceRecord;
import com.example.classpilot.data.entity.ClassEntity;
import com.example.classpilot.data.entity.LessonLog;
import com.example.classpilot.data.entity.Student;

@Database(
        entities = {ClassEntity.class, Student.class, AttendanceRecord.class, LessonLog.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ClassDao classDao();
    public abstract StudentDao studentDao();
    public abstract AttendanceDao attendanceDao();
    public abstract LessonDao lessonDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "classpilot.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}