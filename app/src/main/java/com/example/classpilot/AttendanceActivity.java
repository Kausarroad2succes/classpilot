package com.example.classpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.classpilot.data.entity.Student;
import com.example.classpilot.data.entity.StudentPercentage;
import com.example.classpilot.databinding.ActivityAttendanceBinding;
import com.example.classpilot.databinding.DialogStudentCountBinding;
import com.example.classpilot.ui.StudentAdapter;
import com.example.classpilot.viewmodel.AttendanceViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {
    private static final String TAG = "AttendanceActivity";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_CLASS_NAME = "class_name";

    private ActivityAttendanceBinding binding;
    private AttendanceViewModel viewModel;
    private StudentAdapter adapter;

    private String className;
    private List<Student> currentStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        className = getIntent().getStringExtra(EXTRA_CLASS_NAME);
        if (classId == null) {
            Toast.makeText(this, "No class selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(className);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        viewModel.init(classId);

        String dateText = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
                .format(new Date(viewModel.getSessionDate()));
        binding.tvDate.setText("Attendance for " + dateText);

        adapter = new StudentAdapter(new StudentAdapter.Listener() {
            @Override
            public void onStudentClick(Student s) {
                viewModel.toggle(s.id);
                adapter.refresh();
                updateSummary();
            }

            @Override
            public boolean isPresent(Student s) {
                return viewModel.isPresent(s.id);
            }
        });
        binding.recyclerStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerStudents.setAdapter(adapter);

        viewModel.getStudents().observe(this, list -> {
            currentStudents = list;
            binding.tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            showPage();
            updateSummary();
            // New class with no rolls yet: ask for the number once
            if (list.isEmpty() && !viewModel.isCountPrompted()) {
                viewModel.setCountPrompted();
                showCountDialog();
            }
        });

        viewModel.getPercentages().observe(this, list -> {
            Map<String, StudentPercentage> map = new HashMap<>();
            for (StudentPercentage p : list) {
                map.put(p.studentId, p);
            }
            adapter.setPercentages(map);
        });

        // If today's attendance was already saved, load it once
        viewModel.getTodayRecords().observe(this, records -> {
            if (!viewModel.isSavedMarksLoaded()) {
                viewModel.loadSavedMarks(records);
                adapter.refresh();
                updateSummary();
            }
        });

        binding.tvEmpty.setOnClickListener(v -> showCountDialog());

        binding.btnPrev.setOnClickListener(v -> {
            if (viewModel.getPage() > 0) {
                viewModel.setPage(viewModel.getPage() - 1);
                showPage();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (currentStudents.isEmpty()) {
                Toast.makeText(this, "Set the number of students first", Toast.LENGTH_SHORT).show();
            } else if (viewModel.getPage() >= pageCount() - 1) {
                confirmFinish();
            } else {
                viewModel.setPage(viewModel.getPage() + 1);
                showPage();
            }
        });

        binding.btnAll.setOnClickListener(v -> {
            for (Student s : pageSlice()) {
                viewModel.setPresent(s.id, true);
            }
            adapter.refresh();
            updateSummary();
        });

        binding.btnShare.setOnClickListener(v -> shareSummary());
    }

    private int pageCount() {
        int size = AttendanceViewModel.PAGE_SIZE;
        return Math.max(1, (currentStudents.size() + size - 1) / size);
    }

    private List<Student> pageSlice() {
        int total = currentStudents.size();
        if (total == 0) return new ArrayList<>();
        int size = AttendanceViewModel.PAGE_SIZE;
        int from = viewModel.getPage() * size;
        int to = Math.min(from + size, total);
        return new ArrayList<>(currentStudents.subList(from, to));
    }

    private String rollLabel(Student s) {
        return (s.roll == null || s.roll.isEmpty()) ? "?" : s.roll;
    }

    private void showPage() {
        // Keep the page number valid
        if (viewModel.getPage() > pageCount() - 1) {
            viewModel.setPage(pageCount() - 1);
        }
        List<Student> slice = pageSlice();
        adapter.setStudents(slice);

        if (slice.isEmpty()) {
            binding.tvRange.setText("");
            binding.tvPage.setText("");
        } else {
            binding.tvRange.setText("Rolls " + rollLabel(slice.get(0))
                    + " to " + rollLabel(slice.get(slice.size() - 1)));
            binding.tvPage.setText("Page " + (viewModel.getPage() + 1) + " of " + pageCount());
        }

        binding.btnPrev.setVisibility(viewModel.getPage() == 0 ? View.INVISIBLE : View.VISIBLE);
        boolean last = viewModel.getPage() >= pageCount() - 1;
        binding.btnNext.setText(last ? "Finish" : "Next");
    }

    private int countPresent() {
        int present = 0;
        for (Student s : currentStudents) {
            if (viewModel.isPresent(s.id)) present++;
        }
        return present;
    }

    private void updateSummary() {
        binding.tvSummary.setText("Present: " + countPresent() + " / " + currentStudents.size()
                + "  (tap a row to change)");
    }

    private void showCountDialog() {
        DialogStudentCountBinding d = DialogStudentCountBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(this)
                .setTitle("How many students?")
                .setView(d.getRoot())
                .setPositiveButton("Create rolls", (dialog, which) -> {
                    int n = 0;
                    try {
                        n = Integer.parseInt(d.etCount.getText().toString().trim());
                    } catch (NumberFormatException ignored) {
                    }
                    if (n < 1 || n > 500) {
                        Toast.makeText(this, "Enter a number from 1 to 500", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    viewModel.createRolls(n);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmFinish() {
        int present = countPresent();
        int total = currentStudents.size();
        new AlertDialog.Builder(this)
                .setTitle("Finish attendance?")
                .setMessage("Present: " + present + " / " + total
                        + "\nAbsent: " + (total - present))
                .setPositiveButton("Save", (dialog, which) -> {
                    viewModel.save(currentStudents);
                    Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show();

                    // EXPLICIT INTENT: open Today's lesson and pass the numbers as extras
                    Intent intent = new Intent(AttendanceActivity.this, LessonActivity.class);
                    intent.putExtra(LessonActivity.EXTRA_CLASS_ID,
                            getIntent().getStringExtra(EXTRA_CLASS_ID));
                    intent.putExtra(LessonActivity.EXTRA_CLASS_NAME, className);
                    intent.putExtra(LessonActivity.EXTRA_SESSION_DATE, viewModel.getSessionDate());
                    intent.putExtra(LessonActivity.EXTRA_PRESENT, present);
                    intent.putExtra(LessonActivity.EXTRA_TOTAL, total);
                    startActivity(intent);
                    finish();   // Back from the lesson screen goes to the class menu
                })
                .setNegativeButton("Review", null)
                .show();
    }

    // IMPLICIT INTENT: Android lets the user choose which app receives the text
    private void shareSummary() {
        if (currentStudents.isEmpty()) {
            Toast.makeText(this, "Nothing to share yet", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder absent = new StringBuilder();
        for (Student s : currentStudents) {
            if (!viewModel.isPresent(s.id)) {
                if (absent.length() > 0) absent.append(", ");
                absent.append(rollLabel(s));
            }
        }

        String dateText = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
                .format(new Date(viewModel.getSessionDate()));
        String text = className + " - Attendance\n"
                + dateText + "\n"
                + "Present: " + countPresent() + " / " + currentStudents.size() + "\n"
                + (absent.length() > 0 ? "Absent rolls: " + absent : "Everyone is present.");

        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, className + " attendance");
        send.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(send, "Share attendance summary"));
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