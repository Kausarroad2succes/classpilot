package com.example.classpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.classpilot.data.entity.Student;
import com.example.classpilot.data.entity.StudentPercentage;
import com.example.classpilot.databinding.ActivityReportBinding;
import com.example.classpilot.ui.ReportAdapter;
import com.example.classpilot.ui.ReportRow;
import com.example.classpilot.viewmodel.ReportViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_CLASS_NAME = "class_name";
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_OVERALL = "overall";
    public static final String MODE_LOWEST = "lowest";

    private static final double REQUIRED_PERCENT = 75.0;

    private ActivityReportBinding binding;
    private ReportViewModel viewModel;
    private ReportAdapter adapter;

    private String className;
    private boolean lowestMode;

    private List<Student> students = new ArrayList<>();
    private Map<String, StudentPercentage> percentMap = new HashMap<>();
    private boolean studentsLoaded = false;
    private boolean percentLoaded = false;

    private List<ReportRow> lowRows = new ArrayList<>();   // below 75%, lowest first
    private int classesHeld = 0;

    // Lowest percentage first; same percentage -> smaller roll first
    private final Comparator<ReportRow> lowestFirst = (a, b) -> {
        int c = Double.compare(a.percentage, b.percentage);
        if (c != 0) return c;
        return Integer.compare(rollNumber(a.roll), rollNumber(b.roll));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        className = getIntent().getStringExtra(EXTRA_CLASS_NAME);
        lowestMode = MODE_LOWEST.equals(getIntent().getStringExtra(EXTRA_MODE));
        if (classId == null) {
            Toast.makeText(this, "No class selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(lowestMode ? "Lowest attendance" : "Overall attendance");
            getSupportActionBar().setSubtitle(className);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        viewModel.init(classId);

        adapter = new ReportAdapter();
        binding.recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerReport.setAdapter(adapter);

        // Filter buttons only exist on the Lowest screen
        binding.layoutFilter.setVisibility(lowestMode ? View.VISIBLE : View.GONE);
        updateFilterButtons();
        binding.btnAllFilter.setOnClickListener(v -> {
            viewModel.setBelowOnly(false);
            updateFilterButtons();
            rebuild();
        });
        binding.btnBelowFilter.setOnClickListener(v -> {
            viewModel.setBelowOnly(true);
            updateFilterButtons();
            rebuild();
        });

        viewModel.getStudents().observe(this, list -> {
            students = list;
            studentsLoaded = true;
            rebuild();
        });

        viewModel.getPercentages().observe(this, list -> {
            Map<String, StudentPercentage> map = new HashMap<>();
            for (StudentPercentage p : list) {
                map.put(p.studentId, p);
            }
            percentMap = map;
            percentLoaded = true;
            rebuild();
        });

        binding.btnShare.setOnClickListener(v -> shareReport());
    }

    private void updateFilterButtons() {
        boolean below = viewModel.isBelowOnly();
        binding.btnBelowFilter.setAlpha(below ? 1f : 0.45f);
        binding.btnAllFilter.setAlpha(below ? 0.45f : 1f);
    }

    private int rollNumber(String roll) {
        try {
            return Integer.parseInt(roll);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    private String rollLabel(String roll) {
        return (roll == null || roll.isEmpty()) ? "?" : roll;
    }

    private void rebuild() {
        if (!studentsLoaded || !percentLoaded) return;

        classesHeld = 0;
        for (StudentPercentage p : percentMap.values()) {
            classesHeld = Math.max(classesHeld, p.totalCount);
        }

        List<ReportRow> everyone = new ArrayList<>();     // in roll order
        List<ReportRow> withRecord = new ArrayList<>();
        for (Student s : students) {
            StudentPercentage p = percentMap.get(s.id);
            ReportRow row;
            if (p == null) {
                row = new ReportRow(s.id, s.roll, s.name, false, 0, 0, 0);
            } else {
                row = new ReportRow(s.id, s.roll, s.name, true,
                        p.percentage, p.presentCount, p.totalCount);
                withRecord.add(row);
            }
            everyone.add(row);
        }

        lowRows = new ArrayList<>();
        for (ReportRow r : withRecord) {
            if (r.percentage < REQUIRED_PERCENT) lowRows.add(r);
        }
        Collections.sort(lowRows, lowestFirst);

        List<ReportRow> shown;
        if (!lowestMode) {
            shown = everyone;
        } else if (viewModel.isBelowOnly()) {
            shown = lowRows;
        } else {
            shown = new ArrayList<>(withRecord);
            Collections.sort(shown, lowestFirst);
        }
        adapter.setRows(shown);

        String header;
        String empty = null;
        if (students.isEmpty()) {
            header = "";
            empty = "No students yet.\nOpen Take attendance to create the rolls.";
        } else if (classesHeld == 0) {
            header = "No attendance saved yet";
            if (shown.isEmpty()) empty = "No attendance saved yet.\nTake attendance first.";
        } else {
            header = classesHeld + " classes held  |  " + students.size() + " students";
            if (lowestMode) header += "  |  Below 75%: " + lowRows.size();
            if (shown.isEmpty()) empty = "Nobody is below 75%.";
        }
        binding.tvHeader.setText(header);
        binding.tvEmpty.setText(empty == null ? "" : empty);
        binding.tvEmpty.setVisibility(empty == null ? View.GONE : View.VISIBLE);
    }

    // IMPLICIT INTENT: share the list of students below 75%
    private void shareReport() {
        if (students.isEmpty() || classesHeld == 0) {
            Toast.makeText(this, "Nothing to share yet", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(className).append(" - Attendance report\n");
        sb.append(classesHeld).append(" classes held\n");
        if (lowRows.isEmpty()) {
            sb.append("Nobody is below 75%.");
        } else {
            sb.append("Below 75% (lowest first):\n");
            for (ReportRow r : lowRows) {
                sb.append("Roll ").append(rollLabel(r.roll)).append(" - ")
                        .append(String.format(Locale.getDefault(), "%.1f%%", r.percentage))
                        .append("\n");
            }
        }

        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, className + " attendance report");
        send.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(send, "Share attendance report"));
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