package com.example.classpilot;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.classpilot.data.entity.LessonLog;
import com.example.classpilot.databinding.ActivityLessonBinding;
import com.example.classpilot.ui.LessonAdapter;
import com.example.classpilot.viewmodel.LessonViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LessonActivity extends AppCompatActivity {
    private static final String TAG = "LessonActivity";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_CLASS_NAME = "class_name";
    public static final String EXTRA_SESSION_DATE = "session_date";
    public static final String EXTRA_PRESENT = "present";
    public static final String EXTRA_TOTAL = "total";

    private ActivityLessonBinding binding;
    private LessonViewModel viewModel;
    private LessonAdapter adapter;
    private long sessionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityLessonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Data sent by AttendanceActivity through the EXPLICIT INTENT
        String classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        String className = getIntent().getStringExtra(EXTRA_CLASS_NAME);
        sessionDate = getIntent().getLongExtra(EXTRA_SESSION_DATE, 0L);
        int present = getIntent().getIntExtra(EXTRA_PRESENT, 0);
        int total = getIntent().getIntExtra(EXTRA_TOTAL, 0);

        if (classId == null) {
            Toast.makeText(this, "No class selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Today's lesson");
            getSupportActionBar().setSubtitle(className);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String dateText = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
                .format(new Date(sessionDate));
        binding.tvDate.setText(dateText);

        // The custom view shows today's attendance percentage
        float percent = total == 0 ? 0f : 100f * present / total;
        binding.ring.setPercent(percent);
        binding.tvAttendance.setText("Present " + present + " / " + total
                + "   Absent " + (total - present));

        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);
        viewModel.init(classId, sessionDate);

        adapter = new LessonAdapter();
        binding.recyclerLessons.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerLessons.setAdapter(adapter);

        // If a lesson was already saved today, fill the fields once
        viewModel.getTodayLesson().observe(this, lesson -> {
            if (lesson != null && !viewModel.isPrefilled()) {
                viewModel.setPrefilled();
                binding.etTopic.setText(lesson.topic);
                binding.etNotes.setText(lesson.notes);
            }
        });

        // History list: all earlier days (today's lesson is not repeated here)
        viewModel.getLessons().observe(this, list -> {
            List<LessonLog> previous = new ArrayList<>();
            for (LessonLog l : list) {
                if (l.date != sessionDate) previous.add(l);
            }
            adapter.submit(previous);
            binding.tvNoHistory.setVisibility(previous.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.btnSave.setOnClickListener(v -> saveLesson());
    }

    private void saveLesson() {
        String topic = binding.etTopic.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();
        if (topic.isEmpty()) {
            binding.etTopic.setError("Please type the topic taught today");
            return;
        }
        viewModel.save(topic, notes);
        Toast.makeText(this, "Lesson saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}