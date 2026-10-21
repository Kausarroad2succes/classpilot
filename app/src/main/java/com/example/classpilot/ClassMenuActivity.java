package com.example.classpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.classpilot.databinding.ActivityClassMenuBinding;

public class ClassMenuActivity extends AppCompatActivity {
    private static final String TAG = "ClassMenuActivity";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_CLASS_NAME = "class_name";

    private ActivityClassMenuBinding binding;
    private String classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityClassMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
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
        binding.tvInfo.setText("Choose what to do");

        // EXPLICIT INTENTS: pass the class id + name to the next screen
        binding.cardAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(ClassMenuActivity.this, AttendanceActivity.class);
            intent.putExtra(AttendanceActivity.EXTRA_CLASS_ID, classId);
            intent.putExtra(AttendanceActivity.EXTRA_CLASS_NAME, className);
            startActivity(intent);
        });

        binding.cardOverall.setOnClickListener(v -> openReport(ReportActivity.MODE_OVERALL));
        binding.cardLowest.setOnClickListener(v -> openReport(ReportActivity.MODE_LOWEST));

        // Built after the Today's lesson screen (it needs saved lessons to show)
        binding.cardSyllabus.setOnClickListener(v -> Toast.makeText(this,
                "Syllabus covered comes right after the Today's lesson screen",
                Toast.LENGTH_SHORT).show());
    }

    private void openReport(String mode) {
        Intent intent = new Intent(ClassMenuActivity.this, ReportActivity.class);
        intent.putExtra(ReportActivity.EXTRA_CLASS_ID, classId);
        intent.putExtra(ReportActivity.EXTRA_CLASS_NAME, className);
        intent.putExtra(ReportActivity.EXTRA_MODE, mode);
        startActivity(intent);
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